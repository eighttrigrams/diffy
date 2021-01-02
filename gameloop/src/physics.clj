(ns physics
  (:import (org.dyn4j.dynamics Body World)
           (org.dyn4j.dynamics.joint Joint MotorJoint RevoluteJoint WeldJoint PrismaticJoint)
           (org.dyn4j.geometry Rectangle MassType Vector2 Mass)))

(defonce world (World.))

(defonce joints (atom {}))

(defonce bodies (atom {}))

(defn create-body:rectangle
  ([[x-pos y-pos width height & [rotation] :as t] id]
   (create-body:rectangle t id :normal))
  ([[x-pos y-pos width height & [rotation]] id mass-type]
   (let [body      (Body.)
         rectShape (Rectangle. width height)]
     (.addFixture body rectShape)
     (.setUserData body {:width width :height height :id id :type :rectangle})
     (.setMass body (if (= mass-type :infinite) MassType/INFINITE MassType/NORMAL))
     (when (not (nil? rotation)) (.rotate body rotation))
     (.translate body x-pos y-pos)
     (.addBody world body)
     (swap! bodies assoc id body)

     ;; TODO review: needed?
     [id body])))

(defn get-engine-bodies
  ([] (get-engine-bodies nil))
  ([keys]
   (let [bodies           (.getBodies world)
         map-f            (map
                           (fn prepare-body
                             [^Body body]
                             (let [user-data (.getUserData body)]
                               {:x          (.getTranslationX (.getTransform body))
                                :y          (.getTranslationY (.getTransform body))
                                :properties user-data
                                :body       body
                                :rotation   (- (.getRotationAngle (.getTransform body)))})))
         filter-f         (filter #(.contains keys (-> % :properties :id)))
         xf               (if (nil? keys) map-f (comp map-f filter-f))]
     (if (not bodies)
       '()
       (eduction xf bodies)))))

(defn step-in-ms [tick-in-ms] (.step world tick-in-ms))

;; https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/dynamics/joint/RevoluteJoint.java
(defn create-joint:revolute [id body1-id body2-id [x y]]
  (let [body1 (:body (first (get-engine-bodies [body1-id])))
        body2 (:body (first (get-engine-bodies [body2-id])))
        joint (RevoluteJoint. body1 body2 (Vector2. x y))]
    (.setReferenceAngle joint 0.0)
    (.setLimitEnabled joint true)
    (.setLowerLimit joint -1.5)
    (.setUpperLimit joint 0.1)
    (.addJoint world joint)
    (.setMaximumMotorTorque joint 200.0)
    (.setMotorEnabled joint true)
    (.setMotorSpeed joint 0.05)

    (swap! joints assoc id joint)))

(defn create-joint:weld [id body1-id body2-id [x y]]
  (let [body1 (:body (first (get-engine-bodies [body1-id])))
        body2 (:body (first (get-engine-bodies [body2-id])))
        joint (WeldJoint. body1 body2 (Vector2. x y))]
    (.setCollisionAllowed joint false)
    (.addJoint world joint)

    (swap! joints assoc id joint)))

(defn create-joint:prismatic [id body1-id body2-id [x1 y1] [x2 y2]]
  (let [body1 (:body (first (get-engine-bodies [body1-id]))) ;; TODO fetch from bodies
        body2 (:body (first (get-engine-bodies [body2-id])))
        joint (PrismaticJoint. body1 body2 (Vector2. x1 y1) (Vector2. x2 y2))]
    (.setCollisionAllowed joint false)
    (.setMotorEnabled joint true)
    (.setMaximumMotorForce joint 12000.0)
    (.addJoint world joint)

    (swap! joints assoc id joint)))

(defn get-rotation [id]
  (.getRotationAngle (.getTransform (id @bodies))))

(defn set-motor-speed [joint-id speed]
  (.setMotorSpeed (joint-id @joints) speed))

(defn translate-bodies [ids x y]
  (mapv #(.translate (% @bodies) x y) ids))
