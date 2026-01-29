(ns mblog.syndikert)

;; jeg ønsker å få syndikert innhold inn i feeden på mikrobloggeriet.

;; SPARK FILE fra Julian.
;; Irenes greier fra UX Norge.
;; Anders sine greier fra Substack og/eller Medium
;;
;; Idé:
;; - Regelmessig synk (eller på oppstart) av syndikert innhold
;; - Hente ned titler, en liten blurb, og (et forsøk) på innhold

;; syndikert
;; ... er et fint ord.
;; På samme måte som "fjernkohortene".
;; Mekanismen for henting og oppdatering blir mye av det samme,
;; - ta imot nytt innhold (syndikert->poll, fjernkohortene->motta)
;; - skrive det ned persistert
;; - vise det i feeden.

;; syndikert er en ny kohorttype.
;; - tittel
;; - permanent lenke
;; - ta vare på teksten
;; - putt noe på wayback machine også?
