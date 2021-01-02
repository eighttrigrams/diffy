(ns cartpole.cartpole
  (:require [cartpole :as cp]))

(defn on-tick [{done :done :as state}]
  (prn state)
  (if done
    (do
      (Thread/sleep 1000)
      (assoc state :cmd :reset))
    state))

(defn -main [& args]
  (cp/go on-tick))