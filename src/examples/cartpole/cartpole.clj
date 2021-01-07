(ns examples.cartpole.cartpole
  (:require [gym.envs.cartpole :as cp]
            [diffy.activation :as activation]
            [diffy.net :as diffy]
            [diffy.helpers :as h]
            [diffy.dense :as layer]
            [diffy.layers :as l]
            [examples.cartpole.helpers :refer :all]))

(def EPISODES 1000000)

(def MAX-DURATION 500)

(def MAX-ANGLE 0.14)

(def LEARNING-RATE 0.03)

(defn network []
  (diffy/sequential
   h/rand-initializer
   4
   [(layer/dense) activation/sigmoid 50]
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 100]
   [(layer/dense) activation/sigmoid 2]))

(def EPISODE-START-STATE {:transitions {:obervations [] :actions [] :rewards []}})

(defn prepare [state]
  (-> state
      (update :observations reverse)
      (update :actions reverse)
      (update :rewards discount)))

(defn should-train [{step                  :step
                     train-steps-threshold :train-steps-threshold
                     {rewards :rewards}    :transitions}]
  (and (> (count rewards) 0)
       (> step train-steps-threshold)))

(defn predict [net observation]
  (let [prediction (diffy/predict net observation)]
    (softmax prediction)))

(def loss
  (l/loss-layer (fn [_ Y] Y)))

(defn train [net {observations :observations actions :actions rewards :rewards}]
  (let [converted-actions (mapv
                           (fn [observation action reward]
                             (let [replayed-prediction (predict net observation)
                                   error               (-> replayed-prediction (get action) Math/log (* reward))]
                               (-> [0.0 0.0]
                                   (assoc action error)
                                   (assoc (- 1 action) (- error)))))
                           observations
                           actions
                           rewards)
        mini-batch        (mapv vector observations converted-actions)]
    (diffy/train net loss LEARNING-RATE [mini-batch])))

(defn next-episode [{episode               :episode
                     step                  :step
                     net                   :net
                     train-steps-threshold :train-steps-threshold
                     transitions           :transitions
                     :as                   state}]
  (let [should-train       (should-train state)
        raise-threshold    (and should-train (> step (- train-steps-threshold 10)))]
    (if should-train
      (prn "Episode" episode "Length" step "Train!")
      (prn "Episode" episode "Length" step "Threshold" train-steps-threshold))
    (-> state
        (cond-> should-train (update :net #(train % (prepare transitions))))
        (cond-> raise-threshold (update :train-steps-threshold (fn [e] (+ e 0.5))))
        (merge EPISODE-START-STATE)
        (update :episode inc)
        (assoc :cmd :reset))))

(defn on-tick [{episode      :episode
                observation  :observation
                done         :done
                net          :net
                step         :step
                cmd          :cmd
                keys-pressed :keys-pressed
                :as          state}]

  (if (= episode EPISODES)
    (assoc state :cmd :end)
    (if (or done (> step MAX-DURATION))
      (next-episode state)
      (let [action            (if (not (empty? keys-pressed))
                                (if (.contains keys-pressed \a) 0 1)
                                (second (rand-choice (predict net observation) rand)))]
        (-> state
            (assoc :cmd (if (= 0 action) :left :right))
            (update-in [:transitions :observations] (partial cons observation))
            (update-in [:transitions :actions] (partial cons action))
            (update-in [:transitions :rewards] (partial cons step)))))))

(defn -main [& args]
  (cp/go on-tick
         identity
         (merge EPISODE-START-STATE
                {:episode               0
                 :max-angle             MAX-ANGLE
                 :net                   (network)
                 :train-steps-threshold 10})))
