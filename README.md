# XMLDataQuality

This project contains the data for the thesis 

**Open Government Data Quality Assessment**

by Francesca Iuliano

---

Nowadays the Open Government Data (OGD) are huge assets as they are published by the public administrations with the aim to improve the transparency and the participation of the citizens in the decision making process. The OGD are the result of an increasingly urgent imperative of transparency and accountability. In a period of deep mistrust towards institutions and their representatives, the OGD allow an improvement of relationship among the government and citizens. The OGD are information that are accessible, machine-processable and that can be used and reused without any restriction.  Although they have a very high potential, in practice they also show several problems. One of the problems is their (possible lack of) quality.  Data with high quality are also data that are useful to citizens interested in monitoring how the government manages public money and public resources by increasing the transparency and reducing corruption and illegality, that is why the quality of published Open Government Data has become very important. 

Although many models have been proposed to evaluate the Open Data portals, since the term Open Government Data indicates various type of data provided by the public administrations, it is very difficult to define a general model for the evaluation of such data.

The objectives of this work are 

1.	to build a framework to assess the quality of public contracts, 
2.	to test its effectiveness on a big amount of data and 
3.	to classify governments based on the quality of the published data. 

The data chosen as case study are the summary tables about public contracts provided by some Italian Universities in conformity with the Transparency Decree. To select universities on which to perform the evaluation and on which to draw up the ranking, we used the overall ranking of the best Italian Universities for the 2014 provided by the newspaper 'Il Sole 24 Ore'. 

The work is divided into two parts: the ETL part and the quality assessment part. 
 

## ETL

The ETL consists in extracting data from the XML files provided by each University, transform the collected data in order to make them suitable for quality assessment, loading the data, previously extracted and possibly transformed, in a target database. Since the database had never been designed before, during the ETL part it is designed a database that stores the data about public contracts and that is used as a starting point to conduct the second part of the work, the quality analysis. To develop the first part of the work are chosen the Java technologies, for extracting the data from the XML files, to perform the transformation on the data and to load them in the database and MySql to store the data in relational database. 

## Quality assessment
The quality assessment part consists in defining a model suited to the public contracts analysis and in testing it on the data provided by each University and previously stored in database. Among the different models already present, a new one is defined for the evaluation of the public contracts. The evaluation is thus performed using what can be considered an hybrid model in that it takes into account some intrinsic characteristics of the data, that is, aspects of quality that can be quantified for any type of data and some features that strictly depend on the type of data.

## Results
By implementing in Java the metrics defined by the model, it is possible to detect different problems present in the dataset published by Italian Universities. Only 12 of the 25 selected Universities have published the summary tables for the 2014 in the sub section ‘Bandi di gara e contratti’ of the ‘Amministrazione Trasparente’ section of its corporate website as specified by the Transparent Decree. The evaluation of the published datasets from these 12 Universities brings to light some issues on data quality such as completeness and accuracy errors and many inconsistencies.
In particular it is detected the absence of some basic information such as the contractor selection or the unique identifier of the participants in public contracts. In some other cases some elements, such as the cig (the ID used to uniquely identify a contract), are provided in the summary tables but their values are out of the domain. For some contracts it is detected the absence of a successful tenderer but an amount paid different by zero that lead to a data inconsistency while in some cases it is not possible to understand why public money are spent due to the absence of the information about the description of the contracts.


## Conclusions
Through the evaluation, it is possible to deduce that in most cases the Italian Universities  do not publish the summary tables or they publish them in a format that is not the XML format as required by the ANAC, the Supervisor of public contracts. In general, for the Universities that publishes the summary tables in XML format, the quality of the data is quite high.  Even the University which occupies the last place in the classification  has a fairly high percentage of correct cells. Although the quality is high, there are some errors that are unacceptable, just as the presence of a non-zero amount paid for a contract and the complete absence of the successful tenderer for the same contract or an amount paid greater the award amount specified for a contract. These kind of problems does not allow to trace the public money, that is, it is not possible to determine who receives the money or why more public money than requested are spent. These quality problems together with some accuracy and completeness errors reduce the transparency on the activity of the governments.
In conclusion, the results of the evaluation indicate that some improvements may be made on the dataset published by public administrations. It would also be useful to check whether  the various problems encountered are due to errors in the compilation of summary tables or are due to illegality in the award of public contracts.
