(ns mnist.mnist-common
  (:require [diffy.dense :as layer]
            [diffy.net :as diffy]
            [diffy.helpers :as h]
            [diffy.activation :as activation]
            [diffy.loss :as l]
            [diffy.matrix.matrix :refer :all]))

(defn network []
  (diffy/sequential
   h/rand-initializer
   784
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 50]
   [(layer/dense false) activation/sigmoid 10]))

(defn- max-index [v]
  (first (apply max-key second (map-indexed vector v))))

(defn- evaluate [net test]
  (let [predict                 (partial diffy/predict net)
        correct-predictions     (reduce
                                 (fn [correct-predictions [X Y]]
                                   (if (=
                                        (max-index Y)
                                        (max-index (predict X)))
                                     (inc correct-predictions)
                                     correct-predictions))
                                 0
                                 test)]
    (prn (str "Accuracy: " (/ correct-predictions (float (count test)))))
    net))

(defn build-and-train [[train test]
                       learning-rate
                       partition-size
                       n-epochs]
  (reduce
   (fn [net epoch]
     (prn (str "Epoch: " (inc epoch)))
     (time
      (->
       (diffy/train net
                    l/loss
                    learning-rate
                    (partition partition-size train))
       (evaluate test))))
   (network)
   (range n-epochs)))
