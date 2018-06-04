(ns cardinal.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(defonce base-url "http://numbersapi.com")

(defonce app-state (atom {:text "" :input "" :type "math"}))

(defn format-base-url [url data type]
  (str url "/" data "/" type "?json"))

(defn get-fact []
  (GET (format-base-url base-url (:input @app-state) (:type @app-state))
       {:response-format :json
        :keywords?       true
        :handler
                         (fn [response]
                           (swap! app-state assoc :text (:text response)))})
  (swap! app-state assoc :input ""))

(defn app-input []
  [:input {:type        "text"
           :placeholder "Type a number"
           :value       (:input @app-state)
           :on-change   (fn [e] (swap! app-state assoc :input (.. e -target -value)))}])


(defn app-form []
  [:div
   [:label ""]
   (app-input)
   [:button {:on-click get-fact
             :disabled (= (count (:input @app-state)) 0)} "load fact"]])


(defn app-header []
  [:header "Cardinal"])

(defn app-card [text]
  [:div text])

(defn app []
    [:div
     (app-header)
     [:div
      (app-card (:text @app-state))
      (app-form)]])

(reagent/render-component [app]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)

