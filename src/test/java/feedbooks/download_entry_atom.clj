; every books in feedbooks has an an atom, it has all meta & links to comments,ect

(ns feedbooks.download-entry-atom
  (:use [clojure.contrib.pprint :only [pprint]])
  (:require [clojure.contrib.lazy-xml :as xml])
  (:import [java.io File]
    [java.net URL]
    [feedbooks DownloadManager]))
  

(def seedroot (File. "/tmp/feedbooks/pop"))

(defn download []
  (let [files (filter #(re-find #"opds" (str %)) (file-seq seedroot))]
    (doseq [file files]
      (let [xml-tree (xml/parse-trim file)
            entries (filter #(= (:tag %) :entry) (:content xml-tree))]
        (doseq [entry entries]
          (let [link (filter (fn [e]
                               (and 
                                (= (:tag e) :link)
                                (= (-> e :attrs :type) "application/atom+xml;type=entry")))
                       (:content entry))
                href (-> (first link) :attrs :href)
                filename (last (re-find #"book/(.+)" href))
                uri (URL. href)
                dest (File. (str "/tmp/books/" filename))]
            (DownloadManager/download uri dest)
            (println filename " " href)))))))
