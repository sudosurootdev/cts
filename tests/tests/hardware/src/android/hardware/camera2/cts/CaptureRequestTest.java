/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.hardware.camera2.cts;

import static android.hardware.camera2.cts.CameraTestUtils.*;
import static android.hardware.camera2.CameraCharacteristics.*;

import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.Size;
import android.hardware.camera2.cts.CameraTestUtils.SimpleCaptureListener;
import android.hardware.camera2.cts.testcases.Camera2SurfaceViewTestCase;
import android.util.Log;

import java.util.Arrays;

/**
 * <p>
 * Basic test for camera CaptureRequest key controls.
 * </p>
 * <p>
 * Several test categories are covered: manual sensor control, 3A control,
 * manual ISP control and other per-frame control and synchronization.
 * </p>
 */
public class CaptureRequestTest extends Camera2SurfaceViewTestCase {
    private static final String TAG = "CaptureRequestTest";
    private static final boolean VERBOSE = Log.isLoggable(TAG, Log.VERBOSE);
    private static final int NUM_FRAMES_VERIFIED = 15;
    /** 30ms exposure time must be supported by full capability devices. */
    private static final long DEFAULT_EXP_TIME_NS = 30000000L;
    private static final int DEFAULT_SENSITIVITY = 100;
    private static final int RGGB_COLOR_CHANNEL_COUNT = 4;
    private static final int MAX_SHADING_MAP_SIZE = 64 * 64 * RGGB_COLOR_CHANNEL_COUNT;
    private static final int MIN_SHADING_MAP_SIZE = 1 * 1 * RGGB_COLOR_CHANNEL_COUNT;
    private static final long IGORE_REQUESTED_EXPOSURE_TIME_CHECK = -1L;
    private static final long EXPOSURE_TIME_BOUNDARY_50HZ_NS = 10000000L; // 10ms
    private static final long EXPOSURE_TIME_BOUNDARY_60HZ_NS = 8300000L; // 8.3ms, Approximation.
    private static final long EXPOSURE_TIME_ERROR_MARGIN_NS = 100000L; // 100us, Approximation.
    private static final int SENSITIVITY_ERROR_MARGIN = 10; // 10
    private static final int DEFAULT_NUM_EXPOSURE_TIME_STEPS = 10;
    private static final int DEFAULT_NUM_SENSITIVITY_STEPS = 16;
    private static final int DEFAULT_SENSITIVITY_STEP_SIZE = 100;
    private static final int NUM_RESULTS_WAIT_TIMEOUT = 100;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test black level lock when exposure value change.
     * <p>
     * When {@link CaptureRequest#BLACK_LEVEL_LOCK} is true in a request, the
     * camera device should lock the black level. When the exposure values are changed,
     * the camera may require reset black level Since changes to certain capture
     * parameters (such as exposure time) may require resetting of black level
     * compensation. However, the black level must remain locked after exposure
     * value changes (when requests have lock ON).
     * </p>
     */
    public void testBlackLevelLock() throws Exception {
        for (int i = 0; i < mCameraIds.length; i++) {
            try {
                openDevice(mCameraIds[i]);

                if (!mStaticInfo.isHardwareLevelFull()) {
                    continue;
                }

                SimpleCaptureListener listener = new SimpleCaptureListener();
                CaptureRequest.Builder requestBuilder =
                        mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                // Start with default manual exposure time, with black level being locked.
                requestBuilder.set(CaptureRequest.BLACK_LEVEL_LOCK, true);
                changeExposure(requestBuilder, DEFAULT_EXP_TIME_NS, DEFAULT_SENSITIVITY);

                Size previewSz =
                        getMaxPreviewSize(mCamera.getId(), mCameraManager, PREVIEW_SIZE_BOUND);
                startPreview(requestBuilder, previewSz, listener);

                // No lock OFF state is allowed as the exposure is not changed.
                verifyBlackLevelLockResults(listener, NUM_FRAMES_VERIFIED, /*maxLockOffCnt*/0);

                // Double the exposure time and gain, with black level still being locked.
                changeExposure(requestBuilder, DEFAULT_EXP_TIME_NS * 2, DEFAULT_SENSITIVITY * 2);
                startPreview(requestBuilder, previewSz, listener);

                // Allow at most one lock OFF state as the exposure is changed once.
                verifyBlackLevelLockResults(listener, NUM_FRAMES_VERIFIED, /*maxLockOffCnt*/1);

                stopPreview();
            } finally {
                closeDevice();
            }
        }
    }

    /**
     * Basic lens shading map request test.
     * <p>
     * When {@link CaptureRequest#SHADING_MODE} is set to OFF, no lens shading correction will
     * be applied by the camera device, and an identity lens shading map data
     * will be provided if {@link CaptureRequest#STATISTICS_LENS_SHADING_MAP_MODE} is ON.
     * </p>
     * <p>
     * When {@link CaptureRequest#SHADING_MODE} is set to other modes, lens shading correction
     * will be applied by the camera device. The lens shading map data can be
     * requested by setting {@link CaptureRequest#STATISTICS_LENS_SHADING_MAP_MODE} to ON.
     * </p>
     */
    public void testLensShadingMap() throws Exception {
        for (int i = 0; i < mCameraIds.length; i++) {
            try {
                openDevice(mCameraIds[i]);

                if (!mStaticInfo.isHardwareLevelFull()) {
                    continue;
                }

                SimpleCaptureListener listener = new SimpleCaptureListener();
                CaptureRequest.Builder requestBuilder =
                        mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                // Shading map mode OFF, lensShadingMapMode ON, camera device
                // should output unity maps.
                requestBuilder.set(CaptureRequest.SHADING_MODE, SHADING_MODE_OFF);
                requestBuilder.set(CaptureRequest.STATISTICS_LENS_SHADING_MAP_MODE,
                        STATISTICS_LENS_SHADING_MAP_MODE_ON);

                Size mapSz = mStaticInfo.getCharacteristics().get(LENS_INFO_SHADING_MAP_SIZE);
                Size previewSz =
                        getMaxPreviewSize(mCamera.getId(), mCameraManager, PREVIEW_SIZE_BOUND);

                startPreview(requestBuilder, previewSz, listener);

                verifyShadingMap(listener, NUM_FRAMES_VERIFIED, mapSz, /*mapModeOn*/false);

                // Shading map mode FAST, lensShadingMapMode ON, camera device
                // should output valid maps.
                requestBuilder.set(CaptureRequest.SHADING_MODE, SHADING_MODE_FAST);

                startPreview(requestBuilder, previewSz, listener);

                // Allow at most one lock OFF state as the exposure is changed once.
                verifyShadingMap(listener, NUM_FRAMES_VERIFIED, mapSz, /*mapModeOn*/true);

                // Shading map mode HIGH_QUALITY, lensShadingMapMode ON, camera device
                // should output valid maps.
                requestBuilder.set(CaptureRequest.SHADING_MODE, SHADING_MODE_HIGH_QUALITY);

                startPreview(requestBuilder, previewSz, listener);

                verifyShadingMap(listener, NUM_FRAMES_VERIFIED, mapSz, /*mapModeOn*/true);

                stopPreview();
            } finally {
                closeDevice();
            }
        }
    }

    /**
     * Test {@link CaptureRequest#CONTROL_AE_ANTIBANDING_MODE} control.
     * <p>
     * Test all available anti-banding modes, check if the exposure time adjustment is
     * correct.
     * </p>
     */
    public void testAntiBandingModes() throws Exception {
        for (int i = 0; i < mCameraIds.length; i++) {
            try {
                openDevice(mCameraIds[i]);

                if (!mStaticInfo.isHardwareLevelFull()) {
                    continue;
                }

                SimpleCaptureListener listener = new SimpleCaptureListener();

                byte[] modes = mStaticInfo.getAeAvailableAntiBandingModesChecked();

                Size previewSz =
                        getMaxPreviewSize(mCamera.getId(), mCameraManager, PREVIEW_SIZE_BOUND);

                for (byte mode : modes) {
                    antiBandingTestByMode(listener, previewSz, mode);
                }
            } finally {
                closeDevice();
            }
        }

    }

    /**
     * Test AE mode and lock.
     *
     * <p>
     * For AE lock, when it is locked, exposure parameters shouldn't be changed.
     * For AE modes, each mode should satisfy the per frame controls defined in
     * API specifications.
     * </p>
     */
    public void testAeModeAndLock() throws Exception {
        for (int i = 0; i < mCameraIds.length; i++) {
            try {
                openDevice(mCameraIds[i]);

                // Can only test full capability because test relies on per frame control
                // and synchronization.
                if (!mStaticInfo.isHardwareLevelFull()) {
                    continue;
                }

                Size maxPreviewSz = mOrderedPreviewSizes.get(0); // Max preview size.

                // Update preview surface with given size for all sub-tests.
                updatePreviewSurface(maxPreviewSz);

                // Test aeMode and lock
                byte[] aeModes = mStaticInfo.getAeAvailableModesChecked();
                for (byte mode : aeModes) {
                    aeModeAndLockTestByMode(mode);
                }
            } finally {
                closeDevice();
            }
        }
    }

    /** Test {@link CaptureRequest#FLASH_MODE} control.
     * <p>
     * For each {@link CaptureRequest#FLASH_MODE} mode, test the flash control
     * and {@link CaptureResult#FLASH_STATE} result.
     * </p>
     */
    public void testFlashControl() throws Exception {
        for (int i = 0; i < mCameraIds.length; i++) {
            try {
                openDevice(mCameraIds[i]);

                // Can only test full capability because test relies on per frame control
                // and synchronization.
                if (!mStaticInfo.isHardwareLevelFull()) {
                    continue;
                }

                SimpleCaptureListener listener = new SimpleCaptureListener();
                CaptureRequest.Builder requestBuilder =
                        mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                Size maxPreviewSz = mOrderedPreviewSizes.get(0); // Max preview size.

                startPreview(requestBuilder, maxPreviewSz, listener);

                // Flash control can only be used when the AE mode is ON or OFF.
                flashTestByAeMode(listener, CaptureRequest.CONTROL_AE_MODE_ON);
                flashTestByAeMode(listener, CaptureRequest.CONTROL_AE_MODE_OFF);

                stopPreview();
            } finally {
                closeDevice();
            }
        }
    }

    // TODO: add 3A state machine test.

    /**
     * Test flash mode control by AE mode.
     * <p>
     * Only allow AE mode ON or OFF, because other AE mode could run into conflict with
     * flash manual control. This function expects the camera to already have an active
     * repeating request and be sending results to the listener.
     * </p>
     *
     * @param listener The Capture listener that is used to wait for capture result
     * @param aeMode The AE mode for flash to test with
     */
    private void flashTestByAeMode(SimpleCaptureListener listener, int aeMode) throws Exception {
        CaptureRequest request;
        CaptureResult result;
        final int NUM_FLASH_REQUESTS_TESTED = 10;
        CaptureRequest.Builder requestBuilder = createRequestForPreview();

        if (aeMode == CaptureRequest.CONTROL_AE_MODE_ON) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, aeMode);
        } else if (aeMode == CaptureRequest.CONTROL_AE_MODE_OFF) {
            changeExposure(requestBuilder, DEFAULT_EXP_TIME_NS, DEFAULT_SENSITIVITY);
        } else {
            throw new IllegalArgumentException("This test only works when AE mode is ON or OFF");
        }

        // For camera that doesn't have flash unit, flash state should always be UNAVAILABLE.
        if (mStaticInfo.getFlashInfoChecked() == false) {
            for (int i = 0; i < NUM_FLASH_REQUESTS_TESTED; i++) {
                result = listener.getCaptureResult(CAPTURE_RESULT_TIMEOUT_MS);
                mCollector.expectEquals("No flash unit available, flash state must be UNAVAILABLE"
                        + "for AE mode " + aeMode, CaptureResult.FLASH_STATE_UNAVAILABLE,
                        result.get(CaptureResult.FLASH_STATE));
            }

            return;
        }

        // Test flash SINGLE mode control. Wait for flash state to be READY first.
        waitForResultValue(listener, CaptureResult.FLASH_STATE, CaptureResult.FLASH_STATE_READY,
                NUM_RESULTS_WAIT_TIMEOUT);
        requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
        request = requestBuilder.build();
        mCamera.capture(request, listener, mHandler);
        result = listener.getCaptureResultForRequest(request,
                NUM_RESULTS_WAIT_TIMEOUT);
        // Result mode must be SINGLE, state must be FIRED.
        mCollector.expectEquals("Flash mode result must be SINGLE",
                CaptureResult.FLASH_MODE_SINGLE, result.get(CaptureResult.FLASH_MODE));
        mCollector.expectEquals("Flash state result must be FIRED",
                CaptureResult.FLASH_STATE_FIRED, result.get(CaptureResult.FLASH_STATE));

        // Test flash TORCH mode control.
        CaptureRequest[] requests = new CaptureRequest[NUM_FLASH_REQUESTS_TESTED];
        requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        for (int i = 0; i < NUM_FLASH_REQUESTS_TESTED; i++) {
            requests[i] = requestBuilder.build();
            mCamera.capture(requests[i], listener, mHandler);
        }
        // Verify the results
        for (int i = 0; i < NUM_FLASH_REQUESTS_TESTED; i++) {
            result = listener.getCaptureResultForRequest(requests[i],
                    NUM_RESULTS_WAIT_TIMEOUT);

            // Result mode must be TORCH, state must be FIRED
            mCollector.expectEquals("Flash mode result must be TORCH",
                    CaptureResult.FLASH_MODE_TORCH, result.get(CaptureResult.FLASH_MODE));
            mCollector.expectEquals("Flash state result must be FIRED",
                    CaptureResult.FLASH_STATE_FIRED, result.get(CaptureResult.FLASH_STATE));
        }

        // Test flash OFF mode control
        requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        request = requestBuilder.build();
        mCamera.capture(request, listener, mHandler);
        result = listener.getCaptureResultForRequest(request,
                NUM_RESULTS_WAIT_TIMEOUT);
        mCollector.expectEquals("Flash mode result must be OFF", CaptureResult.FLASH_MODE_OFF,
                result.get(CaptureResult.FLASH_MODE));
    }

    private void verifyAntiBandingMode(SimpleCaptureListener listener, int numFramesVerified,
            int mode, boolean isAeManual, long requestExpTime) throws Exception {
        for (int i = 0; i < numFramesVerified; i++) {
            CaptureResult result = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
            Long resultExpTime = result.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
            assertNotNull("Exposure time shouldn't be null", resultExpTime);
            Integer flicker = result.get(CaptureResult.STATISTICS_SCENE_FLICKER);
            // Scene flicker result should be always available.
            assertNotNull("Scene flicker must not be null", flicker);
            assertTrue("Scene flicker is invalid", flicker >= STATISTICS_SCENE_FLICKER_NONE &&
                    flicker <= STATISTICS_SCENE_FLICKER_60HZ);

            if (isAeManual) {
                // First, round down not up, second, need close enough.
                validateExposureTime(requestExpTime, resultExpTime);
                return;
            }

            long expectedExpTime = resultExpTime; // Default, no exposure adjustment.
            if (mode == CONTROL_AE_ANTIBANDING_MODE_50HZ) {
                // result exposure time must be adjusted by 50Hz illuminant source.
                expectedExpTime =
                        resultExpTime - (resultExpTime % EXPOSURE_TIME_BOUNDARY_50HZ_NS);
            } else if (mode == CONTROL_AE_ANTIBANDING_MODE_60HZ) {
                // result exposure time must be adjusted by 60Hz illuminant source.
                expectedExpTime =
                        resultExpTime - (resultExpTime % EXPOSURE_TIME_BOUNDARY_60HZ_NS);
            } else if (mode == CONTROL_AE_ANTIBANDING_MODE_AUTO){
                /**
                 * Use STATISTICS_SCENE_FLICKER to tell the illuminant source
                 * and do the exposure adjustment.
                 */
                expectedExpTime = resultExpTime;
                if (flicker == STATISTICS_SCENE_FLICKER_60HZ) {
                    expectedExpTime = resultExpTime
                            - (resultExpTime % EXPOSURE_TIME_BOUNDARY_60HZ_NS);
                } else if (flicker == STATISTICS_SCENE_FLICKER_50HZ) {
                    expectedExpTime = resultExpTime
                            - (resultExpTime % EXPOSURE_TIME_BOUNDARY_50HZ_NS);
                }
            }

            if (Math.abs(resultExpTime - expectedExpTime) > EXPOSURE_TIME_ERROR_MARGIN_NS) {
                mCollector.addMessage(String.format("Result exposure time %dns diverges too much"
                        + " from expected exposure time %dns for mode %d when AE is auto",
                        resultExpTime, expectedExpTime, mode));
            }
        }
    }

    private void antiBandingTestByMode(SimpleCaptureListener listener, Size size, int mode)
            throws Exception {
        if(VERBOSE) {
            Log.v(TAG, "Anti-banding test for mode " + mode + " for camera " + mCamera.getId());
        }
        CaptureRequest.Builder requestBuilder =
                mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        requestBuilder.set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE, mode);

        // Test auto AE mode anti-banding behavior
        startPreview(requestBuilder, size, listener);
        verifyAntiBandingMode(listener, NUM_FRAMES_VERIFIED, mode, /*isAeManual*/false,
                IGORE_REQUESTED_EXPOSURE_TIME_CHECK);

        // Test manual AE mode anti-banding behavior
        // 65ms, must be supported by full capability devices.
        final long TEST_MANUAL_EXP_TIME_NS = 65000000L;
        changeExposure(requestBuilder, TEST_MANUAL_EXP_TIME_NS);
        startPreview(requestBuilder, size, listener);
        verifyAntiBandingMode(listener, NUM_FRAMES_VERIFIED, mode, /*isAeManual*/true,
                TEST_MANUAL_EXP_TIME_NS);

        stopPreview();
    }

    /**
     * Test the all available AE modes and AE lock.
     * <p>
     * For manual AE mode, test iterates through different sensitivities and
     * exposure times, validate the result exposure time correctness. For
     * CONTROL_AE_MODE_ON_ALWAYS_FLASH mode, the AE lock and flash are tested.
     * For the rest of the AUTO mode, AE lock is tested.
     * </p>
     *
     * @param mode
     */
    private void aeModeAndLockTestByMode(int mode)
            throws Exception {
        switch (mode) {
            case CONTROL_AE_MODE_OFF:
                // Test manual exposure control.
                aeManualControlTest();
                break;
            case CONTROL_AE_MODE_ON:
            case CONTROL_AE_MODE_ON_AUTO_FLASH:
            case CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
            case CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                // Test AE lock for above AUTO modes.
                aeAutoModeTestLock(mode);
                break;
            default:
                throw new UnsupportedOperationException("Unhandled AE mode " + mode);
        }
    }

    /**
     * Test AE auto modes.
     * <p>
     * Use single request rather than repeating request to test AE lock per frame control.
     * </p>
     */
    private void aeAutoModeTestLock(int mode) throws Exception {
        CaptureRequest.Builder requestBuilder =
                mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        requestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, mode);
        configurePreviewOutput(requestBuilder);

        final int MAX_NUM_CAPTURES_BEFORE_LOCK = 5;
        for (int i = 1; i <= MAX_NUM_CAPTURES_BEFORE_LOCK; i++) {
            autoAeMultipleCapturesThenTestLock(requestBuilder, mode, i);
        }
    }

    /**
     * Issue multiple auto AE captures, then lock AE, validate the AE lock vs.
     * the last capture result before the AE lock.
     */
    private void autoAeMultipleCapturesThenTestLock(
            CaptureRequest.Builder requestBuilder, int aeMode, int numCapturesBeforeLock)
            throws Exception {
        if (numCapturesBeforeLock < 1) {
            throw new IllegalArgumentException("numCapturesBeforeLock must be no less than 1");
        }
        if (VERBOSE) {
            Log.v(TAG, "Camera " + mCamera.getId() + ": Testing auto AE mode and lock for mode "
                    + aeMode + " with " + numCapturesBeforeLock + " captures before lock");
        }

        SimpleCaptureListener listener =  new SimpleCaptureListener();
        CaptureResult latestResult = null;

        CaptureRequest request = requestBuilder.build();
        for (int i = 0; i < numCapturesBeforeLock; i++) {
            // Fire a capture, auto AE, lock off.
            mCamera.capture(request, listener, mHandler);
        }
        // Then fire a capture to lock the AE,
        requestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, true);
        mCamera.capture(requestBuilder.build(), listener, mHandler);

        // Get the latest exposure values of the last AE lock off requests.
        for (int i = 0; i < numCapturesBeforeLock; i++) {
            latestResult = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
        }
        int sensitivity = getValueNotNull(latestResult, CaptureResult.SENSOR_SENSITIVITY);
        long expTime = getValueNotNull(latestResult, CaptureResult.SENSOR_EXPOSURE_TIME);

        // Get the AE lock on result and validate the exposure values.
        latestResult = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
        int sensitivityLocked = getValueNotNull(latestResult, CaptureResult.SENSOR_SENSITIVITY);
        long expTimeLocked = getValueNotNull(latestResult, CaptureResult.SENSOR_EXPOSURE_TIME);
        mCollector.expectEquals("Locked exposure time shouldn't be changed for AE auto mode "
                + aeMode + "after " + numCapturesBeforeLock + " captures", expTime, expTimeLocked);
        mCollector.expectEquals("Locked sensitivity shouldn't be changed for AE auto mode " + aeMode
                + "after " + numCapturesBeforeLock + " captures", sensitivity, sensitivityLocked);
    }

    /**
     * Iterate through exposure times and sensitivities for manual AE control.
     * <p>
     * Use single request rather than repeating request to test manual exposure
     * value change per frame control.
     * </p>
     */
    private void aeManualControlTest()
            throws Exception {
        CaptureRequest.Builder requestBuilder =
                mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CONTROL_AE_MODE_OFF);
        configurePreviewOutput(requestBuilder);
        SimpleCaptureListener listener =  new SimpleCaptureListener();

        long[] expTimes = getExposureTimeTestValues();
        int[] sensitivities = getSensitivityTestValues();
        // Submit single request at a time, then verify the result.
        for (int i = 0; i < expTimes.length; i++) {
            for (int j = 0; j < sensitivities.length; j++) {
                if (VERBOSE) {
                    Log.v(TAG, "Camera " + mCamera.getId() + ": Testing sensitivity "
                            + sensitivities[j] + ", exposure time " + expTimes[i] + "ns");
                }

                changeExposure(requestBuilder, expTimes[i], sensitivities[j]);
                mCamera.capture(requestBuilder.build(), listener, mHandler);

                CaptureResult result = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
                long resultExpTime = getValueNotNull(result, CaptureResult.SENSOR_EXPOSURE_TIME);
                int resultSensitivity = getValueNotNull(result, CaptureResult.SENSOR_SENSITIVITY);
                validateExposureTime(expTimes[i], resultExpTime);
                validateSensitivity(sensitivities[j], resultSensitivity);
            }
        }
        // TODO: Add another case to test where we can submit all requests, then wait for
        // results, which will hide the pipeline latency. this is not only faster, but also
        // test high speed per frame control and synchronization.

    }

    /**
     * Verify black level lock control.
     */
    private void verifyBlackLevelLockResults(SimpleCaptureListener listener, int numFramesVerified,
            int maxLockOffCnt) throws Exception {
        int noLockCnt = 0;
        for (int i = 0; i < numFramesVerified; i++) {
            CaptureResult result = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
            Boolean blackLevelLock = result.get(CaptureResult.BLACK_LEVEL_LOCK);
            assertNotNull("Black level lock result shouldn't be null", blackLevelLock);

            // Count the lock == false result, which could possibly occur at most once.
            if (blackLevelLock == false) {
                noLockCnt++;
            }

            if(VERBOSE) {
                Log.v(TAG, "Black level lock result: " + blackLevelLock);
            }
        }
        assertTrue("Black level lock OFF occurs " + noLockCnt + " times,  expect at most "
                + maxLockOffCnt + " for camera " + mCamera.getId(), noLockCnt <= maxLockOffCnt);
    }

    /**
     * Verify shading map for different shading modes.
     */
    private void verifyShadingMap(SimpleCaptureListener listener, int numFramesVerified,
            Size mapSize, boolean mapModeOn) throws Exception {
        int numElementsInMap = mapSize.getWidth() * mapSize.getHeight() * RGGB_COLOR_CHANNEL_COUNT;
        float[] unityMap = new float[numElementsInMap];
        Arrays.fill(unityMap, 1.0f);

        for (int i = 0; i < numFramesVerified; i++) {
            CaptureResult result = listener.getCaptureResult(WAIT_FOR_RESULT_TIMEOUT_MS);
            float[] map = result.get(CaptureResult.STATISTICS_LENS_SHADING_MAP);
            assertNotNull("Map must not be null", map);
            assertTrue("Map size " + map.length + " must be " + numElementsInMap,
                    map.length == numElementsInMap);
            assertFalse(String.format(
                    "Map size %d should be less than %d", numElementsInMap, MAX_SHADING_MAP_SIZE),
                    numElementsInMap >= MAX_SHADING_MAP_SIZE);
            assertFalse(String.format("Map size %d should be no less than %d", numElementsInMap,
                    MIN_SHADING_MAP_SIZE), numElementsInMap < MIN_SHADING_MAP_SIZE);

            if (mapModeOn) {
                // Map mode is ON, expect to receive a map with all element >= 1.0f

                int badValueCnt = 0;
                // Detect the bad values of the map data.
                for (int j = 0; j < numElementsInMap; j++) {
                    if (Float.isNaN(map[j]) || map[j] < 1.0f) {
                        badValueCnt++;
                    }
                }
                assertEquals("Number of value in the map is " + badValueCnt + " out of "
                        + numElementsInMap, /*expected*/0, /*actual*/badValueCnt);
            } else {
                // Map mode is OFF, expect to receive a unity map.
                assertTrue("Result map " + Arrays.toString(map) + " must be an unity map",
                        Arrays.equals(unityMap, map));
            }
        }
    }

    //----------------------------------------------------------------
    //---------Below are common functions for all tests.--------------
    //----------------------------------------------------------------

    /**
     * Enable exposure manual control and change exposure and sensitivity and
     * clamp the value into the supported range.
     */
    private void changeExposure(CaptureRequest.Builder requestBuilder,
            long expTime, int sensitivity) {
        expTime = mStaticInfo.getExposureClampToRange(expTime);
        sensitivity = mStaticInfo.getSensitivityClampToRange(sensitivity);

        requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CONTROL_AE_MODE_OFF);
        requestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, expTime);
        requestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, sensitivity);
    }
    /**
     * Enable exposure manual control and change exposure time and
     * clamp the value into the supported range.
     *
     * <p>The sensitivity is set to default value.</p>
     */
    private void changeExposure(CaptureRequest.Builder requestBuilder, long expTime) {
        changeExposure(requestBuilder, expTime, DEFAULT_SENSITIVITY);
    }

    /**
     * Enable exposure manual control and change sensitivity and
     * clamp the value into the supported range.
     *
     * <p>The exposure time is set to default value.</p>
     */
    private void changeExposure(CaptureRequest.Builder requestBuilder, int sensitivity) {
        changeExposure(requestBuilder, DEFAULT_EXP_TIME_NS, sensitivity);
    }

    /**
     * Get the exposure time array that contains multiple exposure time steps in
     * the exposure time range.
     */
    private long[] getExposureTimeTestValues() {
        long[] testValues = new long[DEFAULT_NUM_EXPOSURE_TIME_STEPS + 1];
        long maxExpTime = mStaticInfo.getExposureMaximumOrDefault(DEFAULT_EXP_TIME_NS);
        long minxExpTime = mStaticInfo.getExposureMinimumOrDefault(DEFAULT_EXP_TIME_NS);

        long range = maxExpTime - minxExpTime;
        double stepSize = range / (double)DEFAULT_NUM_EXPOSURE_TIME_STEPS;
        for (int i = 0; i < testValues.length; i++) {
            testValues[i] = minxExpTime + (long)(stepSize * i);
            testValues[i] = mStaticInfo.getExposureClampToRange(testValues[i]);
        }

        return testValues;
    }

    /**
     * Get the sensitivity array that contains multiple sensitivity steps in the
     * sensitivity range.
     * <p>
     * Sensitivity number of test values is determined by
     * {@value #DEFAULT_SENSITIVITY_STEP_SIZE} and sensitivity range, and
     * bounded by {@value #DEFAULT_NUM_SENSITIVITY_STEPS}.
     * </p>
     */
    private int[] getSensitivityTestValues() {
        int maxSensitivity = mStaticInfo.getSensitivityMaximumOrDefault(
                DEFAULT_SENSITIVITY);
        int minSensitivity = mStaticInfo.getSensitivityMinimumOrDefault(
                DEFAULT_SENSITIVITY);

        int range = maxSensitivity - minSensitivity;
        int stepSize = DEFAULT_SENSITIVITY_STEP_SIZE;
        int numSteps = range / stepSize;
        // Bound the test steps to avoid supper long test.
        if (numSteps > DEFAULT_NUM_SENSITIVITY_STEPS) {
            numSteps = DEFAULT_NUM_SENSITIVITY_STEPS;
            stepSize = range / numSteps;
        }
        int[] testValues = new int[numSteps + 1];
        for (int i = 0; i < testValues.length; i++) {
            testValues[i] = minSensitivity + stepSize * i;
            testValues[i] = mStaticInfo.getSensitivityClampToRange(testValues[i]);
        }

        return testValues;
    }

    /**
     * Validate the AE manual control exposure time.
     *
     * <p>Exposure should be close enough, and only round down if they are not equal.</p>
     *
     * @param request Request exposure time
     * @param result Result exposure time
     */
    private void validateExposureTime(long request, long result) {
        long expTimeDelta = request - result;
        // First, round down not up, second, need close enough.
        mCollector.expectTrue("Exposture time is invalid for AE manaul control test, request: "
                + request + " result: " + result,
                expTimeDelta < EXPOSURE_TIME_ERROR_MARGIN_NS && expTimeDelta >= 0);
    }

    /**
     * Validate AE manual control sensitivity.
     *
     * @param request Request sensitivity
     * @param result Result sensitivity
     */
    private void validateSensitivity(int request, int result) {
        int sensitivityDelta = request - result;
        // First, round down not up, second, need close enough.
        mCollector.expectTrue("Sensitivity is invalid for AE manaul control test, request: "
                + request + " result: " + result,
                sensitivityDelta < SENSITIVITY_ERROR_MARGIN && sensitivityDelta >= 0);
    }

    private <T> T getValueNotNull(CaptureResult result, Key<T> key) {
        T value = result.get(key);
        assertNotNull("Value of Key " + key.getName() + "shouldn't be null", value);
        return value;
    }
}