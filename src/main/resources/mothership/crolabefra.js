(function () {

    var data = crolabefra.data;

    var mergedResults = {};

    $.each(data, function (lang, benchmarksList) {
        $.each(benchmarksList, function (index, benchmark) {

            if (!mergedResults[benchmark.name]) {
                mergedResults[benchmark.name] = {};
            }

            if (!mergedResults[benchmark.name][lang]) {
                mergedResults[benchmark.name][lang] = {
                    name: lang,
                    data: [benchmark.slowestTime, benchmark.averageTime, benchmark.fastestTime]
                };
            } else {
                console.error("Duplicate benchmark/lang tuple found: " + lang + " : " + benchmark.name + " Ignoring....");
            }

        });
    });

    var benchmarks = [];
    _.each(mergedResults, function (langData, key) {
        var benchmark = {};
        benchmark.name = key;
        benchmark.series = _.values(langData);
        benchmarks.push(benchmark);
    });

    benchmarks = _.sortBy(benchmarks, 'name');

    var nrOfCharts = _.size(benchmarks);

    var chartCounter = 0;

    var $main = $('#main');

    _.forEach(benchmarks, function(benchmark) {
        var $chartBox = $('<div class="col-xs-12 col-md-6"> <div class="chart well"> <h4>'+ benchmark.name + '</h4> <div class="ct-chart ct-golden-section" id="'+ benchmark.name +'"></div> </div> </div>');
        $chartBox.appendTo($main);

        var chart = new Chartist.Bar('#' + benchmark.name, {
                labels: ['slowest run', 'average', 'fastest run'],
                series: benchmark.series
            },
            {
                seriesBarDistance: 20,
                horizontalBars: true,
                axisX: {
                    type: Chartist.AutoScaleAxis,
                    offset: 50,
                    onlyInteger: true
                },
                axisY: {
                    offset: 50,
                    scaleMinSpace: 30
                },
                plugins: [
                    Chartist.plugins.ctAxisTitle({
                        axisY: {
                            axisTitle: ''
                        },
                        axisX: {
                            axisTitle: 'ms per run',
                            axisClass: 'ct-axis-title',
                            offset: {
                                x: 0,
                                y: 50
                            },
                            textAnchor: 'middle'
                        }
                    })
                ]
            }
        );


        // label rendering taken from http://jsbin.com/wacuva/2/edit?css,js,output (created by the chartist author)
        var seriesIndex = -1;
        chart.on('created', function () {
            // reset series counter
            seriesIndex = -1;
        });

        chart.on('draw', function (context) {
            if (context.type === 'bar') {
                if (context.index === 0) {
                    seriesIndex++;
                }

                var seriesName = chart.data.series[seriesIndex].name;

                context.element.root().elem('text', {
                    x: context.x1 + 15,
                    y: context.y2 + 5
                }, 'ct-bar-label').text(seriesName);
            }
        });
    });



}());

