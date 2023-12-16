(ns future.core
  (:gen-class))

(defn -main
  "First steps using futures"
  [& args]
  
  (defn long_calculation [x]
    (Thread/sleep (* x 1000))
    (+ x 1)
    )

  (def f1
    (future (long_calculation 1)))
  (println "Future f1 defined")
  
  (def f2
    (future (long_calculation 5)))
  (println "Future f2 defined")
  
  (println @f2) ; blocks for approx. 5 seconds
  (println @f1) ; returns immediately because the result is available
  
  (shutdown-agents)
  )