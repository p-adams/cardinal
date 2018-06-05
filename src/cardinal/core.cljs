(ns cardinal.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]))

(enable-console-print!)

;; constants, state, and utilities

(defonce base-url "http://numbersapi.com?json")

(defonce app-state (atom {:text    ""
                          :input   ""
                          :type    "math"
                          :month   "1"
                          :day     1
                          :visible false}))

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


(defn url-insert [s sb i]
  "Inserts into url a substring at given index"
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
                           (swap! app-state assoc :visible true)
                           (swap! app-state assoc :text (:text response)))})
  (swap! app-state assoc :input ""))


;; UI Components

(defn app-label [text]
  [:label.block.text-grey-darker.text.md.font-bold.mb-2 {:for text}
   text])

(defn app-input []
  [:input.appearance-none.block.w-full.bg-grey-lighter.text-grey-darker.border.border-grey-lighter.rounded.py-3.px-4.leading-tight
   {:type        "text"
    :placeholder "Type a number..."
    :value       (:input @app-state)
    :on-change   #(swap! app-state assoc :input (-> % .-target .-value))}])


(defn app-select [options key]
  [:select.mb-2.block.appearance-none.w-full.bg-grey-lighter.border.border-grey-lighter.text-grey-darker.px-2.rounded.leading-tight
   {:on-change #(swap! app-state assoc key (-> % .-target .-value))}
   (for [option options]
     [:option {:key (:value option) :value (:value option)} (:name option)])])

(defn app-date-selector []
  [:div
   (app-label "Select month")
   (app-select month-options :month)
   (app-label "Select day")
   (app-select (for [i (range 1 32)] {:value i :name i}) :day)])


(defn app-form []
  [:form.bg-white.shadow-md.rounded.px-8.pt-6.pb-8.mb-4
   [:div.mb-4
    (app-label "Select an option from below")
    (app-select type-options :type)
    (if (= (:type @app-state) "date")
      (app-date-selector)
      [:div.mb-6
       (app-label "Enter number")
       (app-input)])]
   [:div.mb-4
    [:button.border-2.border-red.text-red.font-bold.rounded.px-2.py-2.hover:border-blue-light.hover:text-red-darker
     {:on-click (fn [e]
                  (.preventDefault e)
                  (get-number-data))} "Load data"]]])


(defn app-header []
  [:header.bg-red-dark
   [:h1.text-yellow-lightest.px-2.py-4 "Cardinal"]])

(defn app-card [text]
  [:div.bg-yellow-lighter.px-2.py-4.border-2.border-gray.rounded
   [:button.float-right.border-2.border-red.text-red.font-bold.rounded.hover:border-blue-light.hover:text-red-darker
    {:on-click (fn [] (swap! app-state assoc :visible false))} "close"]
   [:h3 (:type @app-state)]
   [:p (:text @app-state)]])

(defn app-modal []
  [:div {:style
         {:position         "fixed"
          :z-index          999
          :left             "25%"
          :top              "25%"
          :bottom           0
          :right            "25%"
          :overflow         "auto"
          :background-color "rgba(255, 255, 255, 0.15)"
          }}
   (app-card (:text @app-state))])

(defn app []
  [:div.h-screen.bg-yellow-lightest
   (app-header)
   [:div {:style {:margin-top "20px"}}
    (app-form)
    (if (= (:visible @app-state) true) (app-modal))]])

(reagent/render-component [app]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
;; optionally touch your app-state to force rerendering depending on
;; your application
;; (swap! app-state update-in [:__figwheel_counter] inc)