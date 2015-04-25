# -*- Mode: ruby -*-

require 'rubygems'
require 'rake'

def doit(text)
    puts "== " + text
    system(text)
end

def working_directory_clean?
  output = `git status --porcelain`
  output.empty?
end

desc "Codox into gh-pages branch"
task :codox do
  if working_directory_clean?
    doit("lein doc")
    doit("git checkout gh-pages")
    doit("cp -r /var/tmp/suchwow-doc * .")
    doit("git add *")
    doit("git commit -am 'doc update'")
    doit("git push origin gh-pages")
  else
    puts "The working directory is not clean"
    doit("git status")
  end
end

desc "Check many versions of Clojure"
task :compatibility do
  doit("lein compatibility")
end
