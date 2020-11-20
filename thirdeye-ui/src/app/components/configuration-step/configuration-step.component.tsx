import { Box, Card, Grid, Typography } from "@material-ui/core";
import RefreshIcon from "@material-ui/icons/Refresh";
import React, { FunctionComponent } from "react";
import { useTranslation } from "react-i18next";
import { getGraphDataFromAPIData } from "../../utils/chart/chart-util";
import { Button } from "../button/button.component";
import LineChart from "../charts/line-graph.component";
import CommonCodeMirror from "../editor/code-mirror.component";
import { ConfigStepsProps } from "./configuration-step.interfaces";

export const ConfigurationStep: FunctionComponent<ConfigStepsProps> = ({
    name,
    extraFields,
    showPreviewButton,
    config,
    previewData,
    onConfigChange,
    onResetConfig,
    onPreviewAlert,
}: ConfigStepsProps) => {
    const { t } = useTranslation();
    const handlePreviewAlert = (): void => {
        if (typeof onPreviewAlert === "function") {
            onPreviewAlert();
        }
    };

    return (
        <Grid container>
            <Grid item xs={12}>
                <Typography variant="h4">{name}</Typography>
            </Grid>
            <Grid item xs={12}>
                <Box
                    alignItems="center"
                    display="flex"
                    justifyContent="space-between"
                >
                    {extraFields}
                    <Button
                        color="primary"
                        startIcon={<RefreshIcon />}
                        variant="text"
                        onClick={onResetConfig}
                    >
                        {t("label.reset-configuration")}
                    </Button>
                </Box>
            </Grid>
            <Grid item xs={12}>
                <CommonCodeMirror
                    options={{
                        mode: "text/x-yaml",
                        indentWithTabs: true,
                        smartIndent: true,
                        lineNumbers: true,
                        lineWrapping: true,
                        extraKeys: { "'@'": "autocomplete" },
                    }}
                    value={config}
                    onChange={onConfigChange}
                />
            </Grid>
            {showPreviewButton && (
                <Grid item xs={12}>
                    <Box>
                        <Button
                            color="primary"
                            variant="text"
                            onClick={handlePreviewAlert}
                        >
                            {t("label.preview-alert")}
                        </Button>
                    </Box>
                </Grid>
            )}
            {previewData ? (
                <Grid item xs={12}>
                    <Card>
                        <LineChart
                            data={getGraphDataFromAPIData(previewData)}
                        />
                    </Card>
                </Grid>
            ) : null}
        </Grid>
    );
};
