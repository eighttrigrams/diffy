(ns cartpole.cartpole
  (:require [cartpole :as cp]))

(defn on-tick [state]

  (prn state)

  state)

(defn -main [& args]
  (cp/go on-tick))