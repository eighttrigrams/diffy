(ns cartpole.torso
  (:require [torso :as t]))

(defn on-tick [keys-presed]
  (prn keys-presed))

(defn -main [& args]
  (t/go on-tick))