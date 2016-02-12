#! /bin/env Rscript
library(fitdistrplus)
library(logspline)
library(ggplot2)
library(reshape2)
library(plyr)

args<-commandArgs(TRUE)

size <- args[1]
filename <- args[2]
seed <- args[3]

set.seed(seed)

#qos = read.table("ar")
#a = qos[,"V1"]
#r = qos[,"V4"]
#A <- sample(a, size = 158, replace = TRUE)
#R <- sample(r, size = 158, replace = TRUE)
#write.table(a, "~/ar0801.txt", sep = "\t")
#plot(a)
#qplot(a)
#qplot(r)

#descdist(a, discrete = FALSE)

#fit.weibull <- fitdist(a, "weibull")

#plot(fit.weibull)

qos = read.table("ar")
qos$"V1" <- sample(qos$"V1", replace=TRUE)
qos$"V4" <- sample(qos$"V4", replace=TRUE)
qos$"V2" <- NULL
qos$"V3" <- NULL
#list <- melt(qos)
list <- split(qos, seq(nrow(qos)))
#list
list <- sample(list, size=size, replace=TRUE)
#list
sample.qos <- ldply(list, data.frame)
sample.qos$".id" <- NULL
write.table(sample.qos, filename, row.names=FALSE, col.names=FALSE, sep="\t")