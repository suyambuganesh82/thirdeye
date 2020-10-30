import { CssBaseline, ThemeProvider } from "@material-ui/core";
import React, { FunctionComponent } from "react";
import { SWRConfig } from "swr";
import { ApplicationBar } from "./components/application-bar/application-bar.component";
import { AppRouter } from "./routers/app-router";
import { initI18next } from "./utils/i18next.util";
import { theme } from "./utils/material-ui/theme.util";
import { swrFetcher } from "./utils/swr.util";

// Initialize i18next
initI18next();

// ThirdEye UI app
export const App: FunctionComponent = () => {
    return (
        // Apply Meterial UI theme
        <ThemeProvider theme={theme}>
            <CssBaseline />

            <ApplicationBar />

            {/* Initialize SWR config */}
            <SWRConfig value={{ fetcher: swrFetcher }}>
                {/* Router */}
                <AppRouter />
            </SWRConfig>
        </ThemeProvider>
    );
};
