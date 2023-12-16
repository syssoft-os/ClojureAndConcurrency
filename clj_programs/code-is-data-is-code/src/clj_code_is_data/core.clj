(ns clj-code-is-data.core (:gen-class))

; ----------------------------------------------------------------------------
; Data are code :-)
; ----------------------------------------------------------------------------

(def mul_is_better_than_add
  (fn [code]
    (if (list? code)
      (map mul_is_better_than_add code)
      (if (= code '+) '* code)
      )
    )
  )

(def myprog '(+ (- 2 3) (+ 2 (* 4 5))))

(defn -main [& args]
  (println (eval myprog))
  (println (eval (mul_is_better_than_add myprog)))
  )
