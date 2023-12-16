(ns promise.core
  (:gen-class))

(defn -main
  "A simple example using promises"
  [& args]

  (def p (promise))

  ; another thread will deliver the promise ...
  
  (future
    (Thread/sleep 2000)
    (deliver p 42) ; ... excatly after 2 seconds
    )

  (future (println @p))
  
  (println "Additional thread created: ")
  (println "Waiting for the promise to be delivered")
  (println @p)

  (shutdown-agents)
  )
