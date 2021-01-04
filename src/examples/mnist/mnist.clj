(ns examples.mnist.mnist
  (:require [examples.mnist.mnist-common :refer :all]
            [examples.mnist.load-mnist :as mn]
            [diffy.matrix.matrix :refer :all]
            [clojure.core.matrix :as ccm]
            [diffy.matrix.clojure-core-matrix
             :refer  [impl]
             :rename {impl clojure-core-matrix-impl}]))

(defn -main [& args]
  (ccm/set-current-implementation :vectorz)
  (choose-impl! clojure-core-matrix-impl)
  (let [mnist            (mn/go)
        learning-rate    0.005
        partition-size   25
        epochs           25]
    (build-and-train mnist
                     learning-rate
                     partition-size
                     epochs)))
