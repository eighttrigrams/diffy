(ns torso
  (:require
    [runner :as runner]
    [physics :as ph]))

(defn init []
  (ph/create-body:rectangle [50 50 50 2] :arm nil)
  (ph/create-body:rectangle [0 0 50 100] :torso nil)
  (ph/create-body:rectangle [0 -300 1080 10] :floor :infinite)
  (let [joint (ph/create-joint:revolute :arm :torso [25.0 50.0])]
    (ph/translate-bodies [:torso] 150 -230)
    {:joint-ref         joint
     :action-exectuted? false}))

(defn on-tick [state keys-pressed tick-in-ms]
  (reduce
   (fn [state body]
     (if (and (< -0.5 (:rotation body))
              (not (:action-executed? state)))
       (do
         (.setMaximumMotorTorque (:joint-ref state) 42000.0)
         (assoc state :action-exectuted? true))
       state))
   state (ph/get-engine-bodies [:arm]))
  state)

(defn -main [& args]
  (runner/run (init) (partial on-tick)))
