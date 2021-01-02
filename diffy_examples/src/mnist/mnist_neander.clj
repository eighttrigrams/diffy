(ns mnist.mnist-neander
  (:require [mnist.mnist-common :refer :all]
            [mnist.load-mnist :as mn]
            [diffy.matrix.matrix :refer :all]
            [diffy.matrix.neander-matrix
             :refer  [impl]
             :rename {impl neander-matrix-impl}]))

(defn -main [& args]
  (choose-impl! neander-matrix-impl)
  (let [mnist            (mn/go)
        learning-rate    0.005
        partition-size   25
        epochs           25]
    (build-and-train mnist
                     learning-rate
                     partition-size
                     epochs)))
