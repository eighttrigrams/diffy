(ns examples.cartpole.cartpole
  (:require [gym.envs.cartpole :as cp]
            [examples.cartpole.helpers :refer :all]
            [examples.cartpole.net :as net]))

(def EPISODES 10000)

(def MAX-DURATION 200)

(def MAX-ANGLE 0.14)

(def MIN-STEPS-FOR-TRAIN 10)

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

(defn next-episode [{episode               :episode
                     step                  :step
                     train-steps-threshold :train-steps-threshold
                     transitions           :transitions
                     :as                   state}]
  (let [should-train       (should-train state)
        raise-threshold    (and should-train (> step (- train-steps-threshold 10)))]
    (if should-train
      (do (net/train (prepare transitions))
        (prn "Episode" episode "Length" step "Train!"))
      (prn "Episode" episode "Length" step "Threshold" train-steps-threshold))
    (-> state
        (cond-> raise-threshold (update :train-steps-threshold (fn [e] (+ e 0.5))))
        (merge EPISODE-START-STATE)
        (update :episode inc)
        (assoc :cmd :reset))))

(defn on-tick [{episode      :episode
                observation  :observation
                done         :done
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
                                (second (rand-choice (net/predict observation) rand)))]
        (-> state
            (assoc :cmd (if (= 0 action) :left :right))
            (update-in [:transitions :observations] (partial cons observation))
            (update-in [:transitions :actions] (partial cons action))
            (update-in [:transitions :rewards] (partial cons step)))))))

(defn -main [& args]
  (do (net/init-net)
    (cp/go on-tick
           identity
           (merge EPISODE-START-STATE
                  {:episode               0
                   :max-angle             MAX-ANGLE
                   :train-steps-threshold MIN-STEPS-FOR-TRAIN}))))
