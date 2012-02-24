LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libdmtx
LOCAL_SRC_FILES := dmtx.c org_libdmtx_DMTXImage.c
LOCAL_LDLIBS    :=

include $(BUILD_SHARED_LIBRARY)
