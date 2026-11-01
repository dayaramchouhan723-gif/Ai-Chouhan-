const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can read and write their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Dayaram Chouhan",
      premiumActive: true,
      buildType: "Personal Build",
      themeAura: "Crimson Aura",
      iconVariant: "Crimson Wireframe Star",
      language: "Hinglish",
      createdAt: new Date(),
    })
  );

  const doc = await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  // Bob cannot read Alice's profile
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

test("Security Config: owner can set and read config", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("securityConfig").doc("current").set({
      userId: ALICE_UID,
      touchGuardArmed: true,
      appLockEnabled: true,
      motionSensitivity: "High",
      sirenVolume: 90,
      warningVoice: true,
      warningVibration: true,
      warningSiren: true,
      warningFullScreen: true,
      gfModeEnabled: true,
      gfScoldText: "Khabardar phone chhua toh!",
      biometricEnabled: true,
      autoLockTimeoutSeconds: 0,
      voicePersonality: "Dabang Guardian",
      voiceSpeed: 1.0,
      fastResponse: true,
      naturalPause: true,
      emotionalTone: true,
      stealthFaceLock: true,
      intruderPhotoEnabled: true,
      createdAt: new Date(),
    })
  );

  // Bob cannot read Alice's security config
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("securityConfig").doc("current").get()
  );
});

test("Incident Logs: owner can create and read logs", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("incidentLogs").doc("incident_1").set({
      userId: ALICE_UID,
      incidentType: "TOUCH_GUARD_BREACH",
      details: "Phone picked up while armed",
      severity: "HIGH",
      hasLocalPhoto: true,
      timestamp: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("incidentLogs").get()
  );
});
