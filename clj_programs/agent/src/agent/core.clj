(ns agent.core
  (:gen-class))

(import java.util.concurrent.Executors)

(def n 20000000)
(def p 1000)

(def is_prime?
  (fn [v]
    (cond
      (= v 1) false
      (= v 2) true
      (= v 3) true
      (= (mod v 2) 0) false
      :else (loop [d 3]
              (cond (= (mod v d) 0) false
                    (> (* d d) v) true
                    :else (recur (+ d 2))
                    )
              )
      )
    )
  )

(defn count_primes [a b]
     (count (filter is_prime? (range a b))))

(defn partition_range [a b p]
    (map
        (fn [i] [(+ a (* i p)) (+ a (* (+ i 1) p))]) ; Create interval [a+i*p a+(i+1)*p]
        (range 0 (+ (quot (- b a) p) 1))
    )
)

(defn work_package [a i] (+ a (apply count_primes i)))

(defn dispatch [worker intervals]
    (let [n_worker (count worker)]
        (loop [w 0
               intervals intervals]
          (if (seq intervals) ; common idiom to test for non-empty sequences
            (do ; required to execute multiple expressions inside if case
              (send (worker (mod w n_worker)) work_package (first intervals))
              (recur (inc w) (rest intervals))
              )
          )
        )
    )
)

(defn collect [worker]
  (doseq [w worker] (await w))
  (reduce + 0 (map deref worker))
  )

(defn count_primes_with_agents [n p_size n_worker]
     (let [intervals (partition_range 1 n p_size)
           worker (vec (map (fn [_] (agent 0)) (range 0 n_worker)))]
       (dorun (dispatch worker intervals))
       (collect worker)        
       )
)

(defn -main
  [& args]
  (set-agent-send-executor! (Executors/newFixedThreadPool 8))
  (time (println (count_primes_with_agents n p 8)))
  (shutdown-agents)
  )
