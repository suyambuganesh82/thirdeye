import { makeStyles } from "@material-ui/core";

export const useDatasetListStyles = makeStyles({
    datasetList: {
        flex: 1,
        flexWrap: "nowrap", // Fixes layout in Safari
    },
    dataGrid: {
        flex: 1,
        "& .MuiDataGrid-root": {
            minHeight: "100%",
        },
    },
});