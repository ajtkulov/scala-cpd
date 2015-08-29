Scala copy-paste detector
==========

Yet another scala copy-paste detector. 
This is plugin for sbt.

Usage
========

* Add plugin in your `/project/plugins.sbt`: `addSbtPlugin("com.github.ajtkulov" % "scala-cpd % "0.2")`
* Add plugin usage in `/build.sbt`: `val myProject = (project in file(".")).enablePlugins(CpdPlugin)`

Now `cpd` command available in sbt.
```
> cpd
[info] Parse scala files in path: /home/pavel/code/reports-ai/src/main with errorLevel: 10
[info] Created output: /home/pavel/code/reports-ai/target/cpd-result.xml
[success] Total time: 8 s, completed Aug 7, 2015 1:50:18 AM
```

Result located in `project/target/cpd-result.xml` and looks like

```
<cpd>
  <item errorWeigth="22" file1="/home/pavel/code/reports-ai/core/src/main/scala/jobs/PeriodTotalViewsAndBrowsersReportJob.scala" file2="/home/pavel/code/reports-ai/core/src/main/scala/jobs/PeriodKahunaJob.scala" type="Def">
    <code> <![CDATA[override def executeConcrete(sparkContext: Option[SparkContext], jobContext: JobDaoContext): Result = if (children.exists(((x) => x.isNeedRun))) SparkUtils.withSpark(name)(((sc) => children.foreach(((job) => { if (job.isNeedRun) { jobContext.updateCurrentAction(StringContext("executing: ", "").s(job.name)); job.executeConcrete(Some(sc), jobContext) } else (); jobContext.incCompletedSteps() })))) else ()]]> </code>
  </item>
  <item errorWeigth="11" file1="/home/pavel/code/reports-ai/core/src/main/scala/infrastructure/HdfsFileService.scala" file2="/home/pavel/code/reports-ai/core/src/main/scala/infrastructure/HdfsFileService.scala" type="Apply">
    <code> <![CDATA[HdfsFileInfo(x.getPath.getName, path, x.isFile, x.isDirectory, path.substring(hdfsPrefixPath.length), getSize(path, fs), x.getModificationTime)]]> </code>
  </item>
<cpd>
```

Except file
========

* `sbt> cpdGenExceptFile`. Generate default except file.

Format:
```
<cpd>
  <except>
    <![CDATA[code1]]>
  </except>
  <except>
    <![CDATA[code2]]>
  </except>
</cpd>
```

Contains code instances that should be excluded from final results.

Settings
========

* `sbt> cpd --errorLevel 1`. Find code blocks with size specific limit.
* `sbt> cpd --source /path/to/another/project`. Run cpd-tool for specific folder.
* `sbt> cpd --except cpd-except.xml`. Run cpd-tool for specific except file.
