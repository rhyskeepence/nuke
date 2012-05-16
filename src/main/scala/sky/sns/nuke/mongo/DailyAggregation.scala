package sky.sns.nuke.mongo

object DailyAggregation {

  val mapTemplate = """
    function() {
      emit( this._id - (this._id %% 86400000), { %s } )
    }
  """

  val reduceTemplate = """
    function( timestamp, values ) {
      var n = {
        %s
      };

      for ( var i = 0; i < values.length; i++ ){
        %s
      }
      return n;
    }
  """

  val finalizeTemplate = """
    function( timestamp, value ){
      %s
      return value;
    }
  """

  def mapFunctionFor(metricNames: List[String]) = {
    val emitAllMetrics = metricNames.map {
      metric =>
        "%s_count: 1, %s_sum: this.%s".format(metric, metric, metric)
    }.mkString(",")

    mapTemplate.format(emitAllMetrics)
  }


  def reduceFunctionFor(metricNames: List[String]) = {
    val declareMetrics = metricNames.map {
      metric =>
        metric + "_count: 0, " + metric + "_sum: 0"
    }.mkString(", ")

    val countMetrics = metricNames.map {
      metric =>
        "if (n.%s_sum > -1) { n.%s_sum += values[i].%s_sum; n.%s_count += values[i].%s_count; }\n".format(metric, metric, metric, metric, metric)
    }.mkString

    reduceTemplate.format(declareMetrics, countMetrics)
  }

  def finalizeFunctionFor(metricNames: List[String]) = {
    val calculateAverage = metricNames.map {
      metric =>
        "if (value.%s_count > 0) value.%s_avg = value.%s_sum / value.%s_count;\n".format(metric, metric, metric, metric)
    }.mkString

    finalizeTemplate.format(calculateAverage)
  }
}
