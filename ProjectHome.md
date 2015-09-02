# What is SRM-mzQuantML Convertor? #
The **SRM-mzQuantML convertor** is designed to converting output files from **[Skyline](https://brendanx-uw1.gs.washington.edu/labkey/project/home/software/Skyline/begin.view)** (tested on version 1.4 and 2.6.0.6851) to **[mzQuantML](http://www.psidev.info/mzquantml)** format. If Skyline changes its output format in its future version, the convertor will not work until we make it adapted to the changes.


---

## Minimum requirement ##
<font color='red'> The SRM-mzQuantML Convertor is developed using <a href='http://docs.oracle.com/javase/8/javase-clienttechnologies.htm'>JavaFX8</a>. The minimum requirement to successfully run it is to install any version of the <a href='https://java.com/en/download/index.jsp'>Java 8</a>. </font>

# How to use it? #

It is very easy to use **SRM-mzQuantML convetor** by following the steps (assuming **Skyline** has been installed):

> ### Prepare correct Skyline file ###
> > If you don't have Skyline files and want to test the convertor, please scroll down to download the [example csv files](#Example_files.md). In order for the convertor working correctly, users must ensure that they provide correct format of Skyline report CSV file by following the steps below:
    1. Download 'skyline-mzq-report.skyr' file from **[here](https://srm-mzquantml-convertor.googlecode.com/files/skyline-mzq-report.skyr)**.
    1. In Skyline menu, select "File->Export->Report.." to open the **Export Report** window. Click the **Import..** button and import the skyr file just downloaded.
> > > ![http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/import-skyr.png](http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/import-skyr.png)
    1. Make sure **mzQuantML Report** is selected before click **Export** button at the bottom of the window. Save the report as **CSV (Comma delimited) (****.csv)** format.
> > > ![http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/export-to-csv.png](http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/export-to-csv.png)


> ### Convert the CSV file to mzQuantML file ###
    1. Download the latest version of convertor from **[here](http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/release/SRM-MzQuantML-Convertor-1.0.zip)** and unzip the file.
    1. Double click the run.bat file to launch the convertor.![http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/convertor-view.png](http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/main/resources/screenshots/convertor-view.png)
    1. Select the CSV file from the previous step, fill in contact detail (this will make sure producing a valid mzQuantML file) and click the **Convert** button. Choose where the **MzQuantML (****.mzq)** file should be saved. The conversion progress will be shown in the progress bar.

> ### Example files ###
We provide two example files for users to try out the convertor. The files can be downloaded from http://srm-mzquantml-convertor.googlecode.com/svn/trunk/srm-mzquantml-convertor/src/release/examples.zip.

# FAQ #
  1. What happened if a 'Java Virtual Machine Launcher' box appears with message 'Could not find the main class. Program will exit'?
> A: Please reinstall Java and try again. https://www.java.com/en/download/help/error_mainclass.xml