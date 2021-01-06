(ns examples.cartpole.net
  (:require [diffy.activation :as activation]
            [diffy.net :as diffy]
            [diffy.helpers :as h]
            [diffy.dense :as layer]
            [diffy.layers :as l]
            [examples.cartpole.helpers :refer :all]))

(def netw (atom nil))

(defn network []
  (diffy/sequential
   h/rand-initializer
   4
   [(layer/dense) activation/sigmoid 50]
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 2]))

(defn predict [observation]
  (let [prediction (diffy/predict @netw observation)]
    (softmax prediction)))

(def loss
  (l/loss-layer (fn [_ Y] Y)))

(defn train [transitions]
  (let [converted-actions (mapv
                           (fn [observation action reward]
                             (let [replayed-prediction (predict observation)
                                   error               (-> replayed-prediction (get action) Math/log (* reward))]
                               (-> [0.0 0.0]
                                   (assoc action error)
                                   (assoc (- 1 action) (- error)))))
                           (:observations transitions)
                           (:actions transitions)
                           (:rewards transitions))
        mini-batch        (mapv vector (:observations transitions) converted-actions)]
    (reset! netw (diffy/train @netw loss 0.03 [mini-batch]))))

(defn init-net []
  (reset! netw (network)))