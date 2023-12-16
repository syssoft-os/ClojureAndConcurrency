(ns stm.core
  (:gen-class))

(defn join_threads [ts]
  (doall (map (fn [t] (deref t)) ts))
  )

(defn do_the_work [n_threads n_loops f]
  (defn main [] (dotimes [l n_loops] (f)))
  (def threads (map (fn [i] (future (main))) (range 1 (+ n_threads 1))))
  (join_threads threads)
  )

(defn -main
  "Software Transactional Memory (STM) with refs"
  [& args]

  (def n_threads 5)
  (def n_loops 100000)
  
  (println "Using atoms for up and down")
  (def atom_up (atom 0))
  (def atom_down (atom 0))
  (def atom_inconsistencies (atom 0))

  (defn atom_up_and_down []
    ; (dosync ... ) does not help but slows down the program ;-)
    (if (not= (+ @atom_up @atom_down) 0) (swap! atom_inconsistencies + 1))
    (swap! atom_up + 1)
    (swap! atom_down + -1))

  (time (do_the_work n_threads n_loops atom_up_and_down))
  
  (println "Finished with atoms:")
  (println @atom_up)
  (println @atom_down)
  (print "Number of inconsistencies: ")
  (println @atom_inconsistencies)

  (println "Using refs for up and down")
  (def ref_up (ref 0))
  (def ref_down (ref 0))
  (def ref_inconsistencies (atom 0))
  (def ref_dosyncs (atom 0))

  (defn ref_up_and_down []
    (dosync
     (swap! ref_dosyncs + 1)
     (if (not= (+ @ref_up @ref_down) 0) (swap! ref_inconsistencies + 1))
     (alter ref_up + 1)
     (alter ref_down + -1)))

  (time (do_the_work n_threads n_loops ref_up_and_down))

  (println "Finished with refs:")
  (println @ref_up)
  (println @ref_down)
  (print "Number of inconsistencies: ")
  (println @ref_inconsistencies)
  (println "Number of dosyncs:" @ref_dosyncs)

  (shutdown-agents))