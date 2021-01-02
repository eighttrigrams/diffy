(ns cartpole.cartpole
  (:require [cartpole :as cp]))

(defn on-tick [keys-presed]
  (prn keys-presed))

(defn -main [& args]
  (cp/go on-tick))