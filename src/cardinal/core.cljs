(ns cardinal.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(defonce base-url "http://numbersapi.com")

(defonce app-state (atom {:text "" :input "" :type "math" :month "October" :day 4}))

(defonce month-options
         [{:value 1 :name "January"}
          {:value 2 :name "February"}
          {:value 3 :name "March"}
          {:value 4 :name "April"}
          {:value 5 :name "May"}
          {:value 6 :name "June"}
          {:value 7 :name "July"}
          {:value 8 :name "August"}
          {:value 9 :name "September"}
          {:value 10 :name "October"}
          {:value 11 :name "November"}
          {:value 12 :name "December"}])

(defonce type-options
         [{:value "math" :name "Facts about numbers"}
          {:value "trivia" :name "Number trivia"}
          {:value "date" :name "Facts about a date"}])



;; todo: refactor to reduce repetitive code
(defn format-base-url [url input type]
  (cond
    (= type "trivia") (str url "/" input "?json")
    (= type "date") (str url "/" (:month input) "/" (:day input) "?json")
    :else (str url "/" input "/" type "?json")))



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
           :on-change   #(swap! app-state assoc :input (-> % .-target .-value))}])


(defn app-select [options key]
  [:select {:on-change #(swap! app-state assoc key (-> % .-target .-value))}
   (for [option options]
     [:option {:key (:value option) :value (:value option)} (:name option)])])



(defn app-form []
  [:div
   [:p (:type @app-state)]
   (app-select type-options :type)
   (if (= (:type @app-state) "date")
     (app-select (for [i (range 1 32)] {:value i :name i}) :day))
   ;; (app-select)
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

