(ns cardinal.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(defonce base-url "http://numbersapi.com")

(defonce app-state (atom {:fact "" :input "44" :type "math"}))

(defn format-base-url [url data type]
  (str url "/" data "/" type "?json"))

(defn get-fact []
  (GET (format-base-url base-url (:input @app-state) (:type @app-state))
       {:response-format :json
        :keywords?       true
        :handler
                         (fn [response]
                           (swap! app-state assoc :fact (:text response)))}))

(defn app-form [])

(defn app-input []
  [:input {:type "text" :placeholder "Enter a number"}])

(defn app-navbar [])

(defn app []
  (fn []
    [:div
     [:h1 "Cardinal"]
     [:div
      [:p "Fact about 44"]
      [:button {:on-click get-fact} "load fact"]
      [:p (:fact @app-state)]
      (app-input)]]))

(reagent/render-component [app]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)

