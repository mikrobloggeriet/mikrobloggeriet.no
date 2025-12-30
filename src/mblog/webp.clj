(ns mblog.webp
  "Utilities for converting to webp"
  (:require
   [babashka.fs :as fs]
   [babashka.process :as p]
   [clojure.string :as str]))

(defn find-candidates []
  (->> (fs/glob "public" "**/*.{jpg,jpeg,png}")
       (map str)
       (into (sorted-set))))
#_(find-candidates)

(defn find-referencing-files [imgpath]
  (let [{:keys [exit out]} (p/shell {:continue true :out :string} "rg" "-l" imgpath "text")]
    (when (zero? exit)
      (->> out str/split-lines (into (sorted-set))))))
#_(find-referencing-files "/images/Kameldornbaum_Sossusvlei.jpg")

(defn search []
  (->> (find-candidates)
       (keep (fn [image]
               (when-let [refs (find-referencing-files (fs/relativize "public" image))]
                 {:image image :referencing-files refs})))))
#_(search)

(defn compression-quality [image]
  (case (fs/extension image)
    ("jpg" "jpeg") 50 ; JPEGs can handle compression
    "png" 80 ; Don't destroy sharp corners in PNGs
    75))

(defn size-factor [original replacement]
  (format "%.2f" (float (/ (fs/size replacement) (fs/size original)))))

(def cwebp-info
  {:docs "https://developers.google.com/speed/webp/docs/cwebp"
   :binaries "https://developers.google.com/speed/webp/docs/precompiled"
   :brew "brew install webp"})

(defn cwebp [& args]
  ;; Beware: this can mess up orientation for JPEGs with orientation EXIF metadata
  (when-not (fs/which "cwebp")
    (throw (ex-info "cwebp binary not found" cwebp-info)))
  (apply p/shell {:out :string :err :string} "cwebp" args))

(def imagemagick-info
  {:homepage "https://imagemagick.org/"
   :brew "brew install imagemagick"})

(defn imagemagick [& args]
  (when-not (fs/which "magick")
    (throw (ex-info "imagemagick binary not found" imagemagick-info)))
  (apply p/shell {:out :string :err :string} "magick" args))

(defn convert! [from to quality]
  (imagemagick from "-auto-orient" "-quality" quality to)
  ;; (cwebp "-q" quality from "-o" to)
  )

(defn to-webp-file [image]
  (str (fs/strip-ext image) ".webp"))

(comment
  ;; Compression playground
  ;; Image compression on the web is a game.
  ;; - How valuable is small size?
  ;; - Is the compression visible?
  ;; My conclusion: see for yourself.

  (let [jpg "public/images/hostutstillingen-2025-1.jpg"
        webp (to-webp-file jpg)]
    (fs/delete-if-exists webp)
    (convert! jpg webp 50))

  (let [jpg "public/images/hostutstillingen-2025-1.jpg"]
    (size-factor jpg (to-webp-file jpg)))

  (def png "public/images/uferdig-arbeid-mikrobloggeriet.png")
  (let [webp (to-webp-file png)]
    (fs/delete-if-exists webp)
    (imagemagick png "-auto-orient" "-quality" 90 webp))
  (size-factor png (to-webp-file png))

  :=)

(defn probe
  "How much would we gain by compressing this file?"
  {:arglists '[[{:as conversion :keys [image referencing-files]}]]}
  [{:as conversion :keys [image]}]
  (let [quality (compression-quality image)
        compressed (to-webp-file image)]
    (try
      (convert! image compressed quality)
      (assoc conversion
             :compressed compressed
             :quality quality
             :factor (size-factor image compressed))
      (finally (fs/delete compressed)))))

(defn autoconvert
  "Convert to webp, and update references to converted files"
  [{:as conversion :keys [image referencing-files]}]
  (let [quality (compression-quality image)
        compressed (to-webp-file image)
        image-url (str (fs/relativize "public" image))
        new-image-url (str (fs/relativize "public" compressed))]
    (try
      (fs/delete-if-exists compressed)
      (convert! image compressed quality)
      (doseq [f referencing-files]
        (spit f (str/replace (slurp f) image-url new-image-url)))
      (assoc conversion
             :compressed compressed
             :quality quality
             :factor (size-factor image compressed))
      (finally
        ;; (fs/delete image)
        ))))

(defn pmapv [f xs] (into [] (pmap f xs)))

(comment
  ;; How much may size decrease?
  (->> (search)
       (pmapv probe))

  ;; Autoconvert everything
  (->> (search)
       (pmapv autoconvert))

  :=)
