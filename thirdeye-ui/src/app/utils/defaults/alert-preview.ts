const alertPreview = {
    alert: {
        name: "pc5",
        description: "Percentage drop",
        nodes: {
            detection_rule_1: {
                type: "DETECTION",
                subType: "PERCENTAGE_RULE",
                metric: {
                    name: "views",
                    dataset: {
                        name: "pageviews",
                    },
                },
                params: {
                    offset: "wo1w",
                    percentageChange: 0.1,
                    pattern: "down",
                },
            },
        },
        lastTimestamp: 0,
    },
    lastTimestamp: 0,
    detectionEvaluations: {},
    start: 1577865600000,
    end: 1590994800000,
};

export default alertPreview;
