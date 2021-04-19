(ns examples.mnist
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [diffy.dense :as layer]
            [diffy.net :as diffy]
            [diffy.helpers :as h]
            [diffy.activation :as activation]
            [diffy.loss :as l]
            ;;[diffy.matrix.neander-matrix :as neander]
            ;;[diffy.matrix.clojure-matrix :as cm]
            [diffy.matrix.clojure-core-matrix :as ccm]))

(defonce create ccm/create)

(defonce matrix ccm/matrix)

(defn network []
  (diffy/sequential
   h/rand-initializer
   784
   [(layer/dense create matrix) activation/sigmoid 100]
   [(layer/dense create matrix) activation/sigmoid 100]
   [(layer/dense create matrix) activation/sigmoid 50]
   [(layer/dense create matrix false) activation/sigmoid 10]))

(defn load-mnist []
  (with-open [reader (io/reader "resources/mnist_784_csv.csv")]
    (let [mnist (mapv
                 (fn [row]
                   [(matrix (mapv (fn [x] (/ (Float/parseFloat x) 255.0)) (drop-last row)))
                    (matrix
                     (assoc
                      (vec (repeat 10 0.0))
                      (Integer/parseInt (get row 784)) 1.0))])
                 (doall
                  (drop 1 (csv/read-csv reader))))]
      [(take 60000 mnist)
       (drop 60000 mnist)])))

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

(defn -main [& _args]
  (let [mnist            (load-mnist)
        learning-rate    0.005
        partition-size   25
        epochs           25]
    (build-and-train mnist
                     learning-rate
                     partition-size
                     epochs)))
