(ns cardinal.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

(defonce base-url "http://numbersapi.com?json")

(defonce app-state (atom {:text "" :input "" :type "math" :month "1" :day 1}))

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

;; inserts into url a substring at given index
(defn url-insert [s sb i]
  (str (subs s 0 i) "/" sb (subs s i)))

(defn get-url [url input type]
  (let [iq (clojure.string/index-of base-url "?")
        date-string (str (:month @app-state) "/" (:day @app-state) "/date")]
  (cond
    (= type "trivia") (url-insert base-url input iq)
    (= type "date") (url-insert base-url date-string iq)
    :else (url-insert base-url (str input "/" type) iq))))


(defn get-number-data []
  (GET (get-url base-url (:input @app-state) (:type @app-state))
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

(defn app-date-selector []
  [:div  (app-select month-options :month)
  (app-select (for [i (range 1 32)] {:value i :name i}) :day)])



(defn app-form []
  [:div
   [:p (:month @app-state)]
   (app-select type-options :type)
   (if (= (:type @app-state) "date")
     (app-date-selector))
   [:label ""]
   (app-input)
   [:button {:on-click get-number-data} "Load data"]])



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

