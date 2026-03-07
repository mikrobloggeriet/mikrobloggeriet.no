(ns mblog.pandoc-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [mblog.pandoc :as pandoc]))

(defonce pandoc-test? false)

(comment
  ;; Skru på pandoc-testene hvis du vil TDD-e Pandoc!
  (def pandoc-test? true)

  )

(comment
  ;; Bug: Tittelen "“Hvorfor?” er bra!" gir feil overskrift i lista,
  ;; Jeg ser bare "er bra!" i prod.

  ;; Konklusjon:
  ;; Pandoc 3.9 (siste per 2026-02-28) har IKKE bugen
  ;; Pandoc 3.3 (på MB per 2026-02-28) HAR bugen
  ;;
  ;; mblog.pandoc/el->plaintext har ikke noen case for :t "Quoted".

  (def ir (pandoc/from-markdown "# “Hvorfor?” er bra!"))

  (pandoc/to-plain ir)

  (pandoc/header->plaintext {:t "Header",
                             :c
                             [1
                              ["hvorfor-er-bra" [] []]
                              [{:t "Str", :c "“Hvorfor?”"}
                               {:t "Space"}
                               {:t "Str", :c "er"}
                               {:t "Space"}
                               {:t "Str", :c "bra!"}]]})

  (pandoc/from-markdown "# “Hvorfor?” er bra!")
  ;; På Teodor-mac =>
  {:pandoc-api-version [1 23 1 1],
   :meta {},
   :blocks
   [{:t "Header",
     :c
     [1
      ["hvorfor-er-bra" [] []]
      [{:t "Str", :c "“Hvorfor?”"}
       {:t "Space"}
       {:t "Str", :c "er"}
       {:t "Space"}
       {:t "Str", :c "bra!"}]]}]}

  ;; På Mikrobloggeriet sin Pandoc
  (pandoc/from-markdown "# “Hvorfor?” er bra!")
  {:pandoc-api-version [1 23 1],
   :meta {},
   :blocks
   [{:t "Header",
     :c
     [1
      ["hvorfor-er-bra" [] []]
      [{:t "Quoted", :c [{:t "DoubleQuote"} [{:t "Str", :c "Hvorfor?"}]]}
       {:t "Space"}
       {:t "Str", :c "er"}
       {:t "Space"}
       {:t "Str", :c "bra!"}]]}]}

  )

(defmacro ptest [& body]
  `(when pandoc-test?
     ~@body))

(ptest
 (deftest markdown
   (testing "markdown-> output looks like sane pandoc json"
     (let [markdown "hei\npå deg!"]
       (is (map? (pandoc/from-markdown markdown)))
       (is (contains? (pandoc/from-markdown markdown)
                      :pandoc-api-version))))
   (testing "we can roundtrip from and to markdown"
     (let [markdown "hei\n\npå deg!\n"]
       (is (= markdown
              (-> markdown
                  pandoc/from-markdown
                  pandoc/to-markdown)))))
   (testing "but roundtripping only works exactly with a single trailing newline"
     (let [markdown-no-newline "hei\n\npå deg!"]
       (is (not= markdown-no-newline
                 (-> markdown-no-newline
                     pandoc/from-markdown
                     pandoc/to-markdown))))
     (let [markdown-two-newlines "hei\n\npå deg!\n\n"]
       (is (not= markdown-two-newlines
                 (-> markdown-two-newlines
                     pandoc/from-markdown
                     pandoc/to-markdown)))))))

(ptest
 (deftest rst
   (is (= "hei, *du*!"
          (-> "hei, *du*!"
              pandoc/from-rst
              pandoc/to-markdown
              str/trim)))))

(ptest
  (deftest convert-test
    (is (= "<p><em>teodor</em></p>" (-> "_teodor_" pandoc/from-markdown pandoc/to-html str/trim)))))

(ptest
  (deftest el->plaintext-test
    (is (= "hei du"
           (-> "hei _du_" pandoc/from-markdown :blocks first pandoc/el->plaintext)))

    (testing "Handles soft line breaks"
      (is (= "hei du"
             (-> "hei\ndu" pandoc/from-markdown :blocks first pandoc/el->plaintext))))

    (testing "Handles code inside titles"
      (is (= (-> "# OLORM-45: `--scale` i Docker Compose"
                 pandoc/from-markdown
                 :blocks
                 first
                 pandoc/header->plaintext)
             "OLORM-45: --scale i Docker Compose")))

    #_
    (testing "Handles Pandoc 3.3 Quoted"
      (is (= (-> {:t "Quoted", :c [{:t "DoubleQuote"} [{:t "Str", :c "Hvorfor?"}]]}
                 pandoc/el->plaintext)
             ))
      )))

(ptest
  (deftest title-test
    (let [doc "% ABOUT TIME

About time we got some shit done."]
      (is (= "ABOUT TIME"
             (-> doc pandoc/from-markdown pandoc/title))))))

(ptest
  (deftest standalone-is-required-to-keep-metadata
    (testing "Without standalone, title is lost"
      (let [title "The Great Title"]
        (is (nil? (-> "A great document of great items."
                      (pandoc/from-markdown)
                      (pandoc/set-title title)
                      (pandoc/to-markdown)
                      (pandoc/from-markdown)
                      (pandoc/title))))))

    (testing "With standalone, title is kept."
      (testing "for markdown"
        (let [title "The Great Title"]
          (is (= title
                 (-> "A great document of great items."
                     (pandoc/from-markdown)
                     (pandoc/set-title title)
                     (pandoc/to-markdown-standalone)
                     (pandoc/from-markdown)
                     (pandoc/title))))))
      (testing "for html"
        (let [title "The Great Title"]
          (is (= title
                 (-> "A great document of great items."
                     (pandoc/from-markdown)
                     (pandoc/set-title title)
                     (pandoc/to-html-standalone)
                     (pandoc/from-html)
                     (pandoc/title)))))))))

(ptest
  (deftest org-test
    (testing "We can roundtrip text with org-mode"
      (let [org-text "hei /du/"]
        (is
         (= org-text
            (-> org-text
                pandoc/from-org
                pandoc/to-org
                str/trim)))))

    (testing "With org-standalone, we can keep title information"
      (let [title "THE BEST TITLE"]
        (is
         (= title
            (-> "hei _du_"
                pandoc/from-markdown
                (pandoc/set-title title)
                (pandoc/to-org-standalone)
                (pandoc/from-org)
                pandoc/title)))))))

(ptest
  (deftest h1-plaintext-test
    (is (= 1 1))))

(ptest
  (deftest h1-test
    (let [h1-el {:t "Header",
                 :c
                 [1
                  ["super-duper-document" [] []]
                  [{:t "Str", :c "super"}
                   {:t "Space"}
                   {:t "Str", :c "duper"}
                   {:t "Space"}
                   {:t "Str", :c "document"}]]}]
      (is (pandoc/h1? h1-el))
      (is (= "super duper document"
             (pandoc/header->plaintext h1-el))))))

(ptest
  (deftest infer-title-test
    (let [doc (pandoc/from-markdown "# super duper document")]
      (is (= "super duper document"
             (pandoc/infer-title doc))))))

(ptest
  (deftest infer-description-test
    (let [doc (pandoc/from-markdown "# Title

The description.

The rest of the document.")]
      (is (= "The description."
             (pandoc/infer-description doc))))))

(comment
  (require '[mblog.state]
           '[datomic.api :as d])
  (def olorm-58 (d/entity mblog.state/datomic [:doc/slug "olorm-58"]))
  (-> (:doc/markdown olorm-58)
      pandoc/from-markdown))
