import { cloneDeep } from "lodash";
import {
    SubscriptionGroupAlert,
    SubscriptionGroupCardData,
} from "../../components/entity-cards/subscription-group-card/subscription-group-card.interfaces";
import { Alert } from "../../rest/dto/alert.interfaces";
import { SubscriptionGroup } from "../../rest/dto/subscription-group.interfaces";
import {
    createEmptySubscriptionGroup,
    createEmptySubscriptionGroupAlert,
    createEmptySubscriptionGroupCardData,
    filterSubscriptionGroups,
    getSubscriptionGroupAlert,
    getSubscriptionGroupAlertId,
    getSubscriptionGroupAlertName,
    getSubscriptionGroupAlerts,
    getSubscriptionGroupCardData,
    getSubscriptionGroupCardDatas,
} from "./subscription-groups.util";

jest.mock("i18next", () => ({
    t: jest.fn().mockImplementation((key) => key),
}));

describe("Subscription Groups Util", () => {
    test("createEmptySubscriptionGroup should create appropriate subscription group", () => {
        expect(createEmptySubscriptionGroup()).toEqual(
            mockEmptySubscriptionGroup
        );
    });

    test("createEmptySubscriptionGroupCardData should create appropriate subscription group card data", () => {
        expect(createEmptySubscriptionGroupCardData()).toEqual(
            mockEmptySubscriptionGroupCardData
        );
    });

    test("createEmptySubscriptionGroupAlert should create appropriate subscription group alert", () => {
        expect(createEmptySubscriptionGroupAlert()).toEqual(
            mockEmptySubscriptionGroupAlert
        );
    });

    test("getSubscriptionGroupCardData should return empty subscription group card data for invalid subscription group", () => {
        expect(
            getSubscriptionGroupCardData(
                (null as unknown) as SubscriptionGroup,
                mockAlerts
            )
        ).toEqual(mockEmptySubscriptionGroupCardData);
    });

    test("getSubscriptionGroupCardData should return appropriate subscription group card data for subscription group and invalid alerts", () => {
        const expectedSubscriptionGroupCardData = cloneDeep(
            mockSubscriptionGroupCardData1
        );
        expectedSubscriptionGroupCardData.alerts = [];

        expect(
            getSubscriptionGroupCardData(
                mockSubscriptionGroup1,
                (null as unknown) as Alert[]
            )
        ).toEqual(expectedSubscriptionGroupCardData);
    });

    test("getSubscriptionGroupCardData should return appropriate subscription group card data for subscription group and empty alerts", () => {
        const expectedSubscriptionGroupCardData = cloneDeep(
            mockSubscriptionGroupCardData1
        );
        expectedSubscriptionGroupCardData.alerts = [];

        expect(
            getSubscriptionGroupCardData(mockSubscriptionGroup1, [])
        ).toEqual(expectedSubscriptionGroupCardData);
    });

    test("getSubscriptionGroupCardData should return appropriate subscription group card data for subscription group and alerts", () => {
        expect(
            getSubscriptionGroupCardData(mockSubscriptionGroup1, mockAlerts)
        ).toEqual(mockSubscriptionGroupCardData1);
    });

    test("getSubscriptionGroupCardDatas should return empty array for invalid subscription groups", () => {
        expect(
            getSubscriptionGroupCardDatas(
                (null as unknown) as SubscriptionGroup[],
                mockAlerts
            )
        ).toEqual([]);
    });

    test("getSubscriptionGroupCardDatas should return empty array for empty subscription groups", () => {
        expect(getSubscriptionGroupCardDatas([], mockAlerts)).toEqual([]);
    });

    test("getSubscriptionGroupCardDatas should return appropriate subscription group card data array for subscription groups and invalid alerts", () => {
        const expectedSubscriptionGroupCardData1 = cloneDeep(
            mockSubscriptionGroupCardData1
        );
        expectedSubscriptionGroupCardData1.alerts = [];
        const expectedSubscriptionGroupCardData2 = cloneDeep(
            mockSubscriptionGroupCardData2
        );
        expectedSubscriptionGroupCardData2.alerts = [];
        const expectedSubscriptionGroupCardData3 = cloneDeep(
            mockSubscriptionGroupCardData3
        );
        expectedSubscriptionGroupCardData3.alerts = [];

        expect(
            getSubscriptionGroupCardDatas(
                mockSubscriptionGroups,
                (null as unknown) as Alert[]
            )
        ).toEqual([
            expectedSubscriptionGroupCardData1,
            expectedSubscriptionGroupCardData2,
            expectedSubscriptionGroupCardData3,
        ]);
    });

    test("getSubscriptionGroupCardDatas should return appropriate subscription group card data array for subscription groups and empty alerts", () => {
        const expectedSubscriptionGroupCardData1 = cloneDeep(
            mockSubscriptionGroupCardData1
        );
        expectedSubscriptionGroupCardData1.alerts = [];
        const expectedSubscriptionGroupCardData2 = cloneDeep(
            mockSubscriptionGroupCardData2
        );
        expectedSubscriptionGroupCardData2.alerts = [];
        const expectedSubscriptionGroupCardData3 = cloneDeep(
            mockSubscriptionGroupCardData3
        );
        expectedSubscriptionGroupCardData3.alerts = [];

        expect(
            getSubscriptionGroupCardDatas(mockSubscriptionGroups, [])
        ).toEqual([
            expectedSubscriptionGroupCardData1,
            expectedSubscriptionGroupCardData2,
            expectedSubscriptionGroupCardData3,
        ]);
    });

    test("getSubscriptionGroupCardDatas should return appropriate subscription group card data array for subscription groups and alerts", () => {
        expect(
            getSubscriptionGroupCardDatas(mockSubscriptionGroups, mockAlerts)
        ).toEqual(mockSubscriptionGroupCardDatas);
    });

    test("getSubscriptionGroupAlert should return empty subscription group alert for invalid alert", () => {
        expect(getSubscriptionGroupAlert((null as unknown) as Alert)).toEqual(
            mockEmptySubscriptionGroupAlert
        );
    });

    test("getSubscriptionGroupAlert should return appropriate subscription group alert for alert", () => {
        expect(getSubscriptionGroupAlert(mockAlert1)).toEqual(
            mockSubscriptionGroupAlert1
        );
    });

    test("getSubscriptionGroupAlerts should return empty array for invalid alerts", () => {
        expect(
            getSubscriptionGroupAlerts((null as unknown) as Alert[])
        ).toEqual([]);
    });

    test("getSubscriptionGroupAlerts should return empty array for empty alerts", () => {
        expect(getSubscriptionGroupAlerts([])).toEqual([]);
    });

    test("getSubscriptionGroupAlerts should return appropriate subscription group alert array for alerts", () => {
        expect(getSubscriptionGroupAlerts(mockAlerts)).toEqual(
            mockSubscriptionGroupAlerts
        );
    });

    test("getSubscriptionGroupAlertId should return -1 for invalid subscription group alert", () => {
        expect(
            getSubscriptionGroupAlertId(
                (null as unknown) as SubscriptionGroupAlert
            )
        ).toEqual(-1);
    });

    test("getSubscriptionGroupAlertId should return approopriate id for subscription group alert", () => {
        expect(
            getSubscriptionGroupAlertId(mockSubscriptionGroupAlert1)
        ).toEqual(2);
    });

    test("getSubscriptionGroupAlertName should return empty string for invalid subscription group alert", () => {
        expect(
            getSubscriptionGroupAlertName(
                (null as unknown) as SubscriptionGroupAlert
            )
        ).toEqual("");
    });

    test("getSubscriptionGroupAlertName should return approopriate name for subscription group alert", () => {
        expect(
            getSubscriptionGroupAlertName(mockSubscriptionGroupAlert1)
        ).toEqual("testNameAlert2");
    });

    test("filterSubscriptionGroups should return empty array for invalid subscription group card data array", () => {
        expect(
            filterSubscriptionGroups(
                (null as unknown) as SubscriptionGroupCardData[],
                mockSearchWords
            )
        ).toEqual([]);
    });

    test("filterSubscriptionGroups should return empty array for empty subscription group card data array", () => {
        expect(filterSubscriptionGroups([], mockSearchWords)).toEqual([]);
    });

    test("filterSubscriptionGroups should return appropriate subscription group card data array for subscription group card data array and invalid search words", () => {
        expect(
            filterSubscriptionGroups(
                mockSubscriptionGroupCardDatas,
                (null as unknown) as string[]
            )
        ).toEqual(mockSubscriptionGroupCardDatas);
    });

    test("filterSubscriptionGroups should return appropriate subscription group card data array for subscription group card data array and empty search words", () => {
        expect(
            filterSubscriptionGroups(mockSubscriptionGroupCardDatas, [])
        ).toEqual(mockSubscriptionGroupCardDatas);
    });

    test("filterSubscriptionGroups should return appropriate subscription group card data array for subscription group card data array and search words", () => {
        expect(
            filterSubscriptionGroups(
                mockSubscriptionGroupCardDatas,
                mockSearchWords
            )
        ).toEqual([
            mockSubscriptionGroupCardData1,
            mockSubscriptionGroupCardData3,
        ]);
    });
});

const mockEmptySubscriptionGroup = {
    name: "",
    alerts: [],
    notificationSchemes: {
        email: {
            to: [],
        },
    },
};

const mockEmptySubscriptionGroupCardData = {
    id: -1,
    name: "label.no-data-marker",
    alerts: [],
    emails: [],
    subscriptionGroup: null,
};

const mockEmptySubscriptionGroupAlert = {
    id: -1,
    name: "label.no-data-marker",
};

const mockSubscriptionGroup1 = {
    id: 1,
    name: "testNameSubscriptionGroup1",
    alerts: [
        {
            id: 2,
        },
        {
            id: 3,
        },
        {
            id: 4,
        },
    ],
    notificationSchemes: {
        email: {
            to: [
                "testEmail1SubscriptionGroup1",
                "testEmail2SubscriptionGroup1",
            ],
        },
    },
} as SubscriptionGroup;

const mockSubscriptionGroup2 = {
    id: 5,
    alerts: [] as Alert[],
    notificationSchemes: {},
} as SubscriptionGroup;

const mockSubscriptionGroup3 = {
    id: 6,
    name: "testNameSubscriptionGroup6",
} as SubscriptionGroup;

const mockSubscriptionGroups = [
    mockSubscriptionGroup1,
    mockSubscriptionGroup2,
    mockSubscriptionGroup3,
];

const mockAlert1 = {
    id: 2,
    name: "testNameAlert2",
} as Alert;

const mockAlert2 = {
    id: 3,
} as Alert;

const mockAlert3 = {
    id: 6,
    name: "testNameAlert6",
} as Alert;

const mockAlerts = [mockAlert1, mockAlert2, mockAlert3];

const mockSubscriptionGroupAlert1 = {
    id: 2,
    name: "testNameAlert2",
};

const mockSubscriptionGroupAlert2 = {
    id: 3,
    name: "label.no-data-marker",
};

const mockSubscriptionGroupAlert3 = {
    id: 6,
    name: "testNameAlert6",
};

const mockSubscriptionGroupAlerts = [
    mockSubscriptionGroupAlert1,
    mockSubscriptionGroupAlert2,
    mockSubscriptionGroupAlert3,
];

const mockSubscriptionGroupCardData1 = {
    id: 1,
    name: "testNameSubscriptionGroup1",
    alerts: [
        {
            id: 2,
            name: "testNameAlert2",
        },
        {
            id: 3,
            name: "label.no-data-marker",
        },
    ],
    emails: ["testEmail1SubscriptionGroup1", "testEmail2SubscriptionGroup1"],
    subscriptionGroup: mockSubscriptionGroup1,
};

const mockSubscriptionGroupCardData2 = {
    id: 5,
    name: "label.no-data-marker",
    alerts: [],
    emails: [],
    subscriptionGroup: mockSubscriptionGroup2,
};

const mockSubscriptionGroupCardData3 = {
    id: 6,
    name: "testNameSubscriptionGroup6",
    alerts: [],
    emails: [],
    subscriptionGroup: mockSubscriptionGroup3,
};

const mockSubscriptionGroupCardDatas = [
    mockSubscriptionGroupCardData1,
    mockSubscriptionGroupCardData2,
    mockSubscriptionGroupCardData3,
];

const mockSearchWords = ["testNameAlert2", "testNameSubscriptionGroup6"];
