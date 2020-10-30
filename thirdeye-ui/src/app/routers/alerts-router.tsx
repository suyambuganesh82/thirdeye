import React, { FunctionComponent } from "react";
import { Redirect, Route, Switch } from "react-router-dom";
import { AlertsAllPage } from "../pages/alerts-all-page/alerts-all-page.component";
import { AppRoute, getAlertsAllPath } from "../utils/routes.util";

// ThirdEye UI alerts path router
export const AlertsRouter: FunctionComponent = () => {
    return (
        <Switch>
            {/* Alerts path */}
            <Route exact path={AppRoute.ALERTS}>
                {/* Redirect to alerts - all path */}
                <Redirect to={getAlertsAllPath()} />
            </Route>

            {/* Alerts - all path */}
            <Route exact component={AlertsAllPage} path={AppRoute.ALERTS_ALL} />
        </Switch>
    );
};
