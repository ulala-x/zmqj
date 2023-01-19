cmake_minimum_required(VERSION 3.25)

project(
    "ZMQJ-JNI"
    VERSION 1.0.0
    LANGUAGES C CXX)



set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)


#Options
option(ENABLE_WARNINGS "Enable to add warnings to a target." ON)
option(ENABLE_WARNINGS_AS_ERRORS "Enable to treat warnings as errors." OFF)
option(ENABLE_LTO "Enable to add Link Time Optimization." ON)

#Project/Library Names
set(ZMQJ "zmqj")
set(UNIT_TEST_NAME "unit_tests")
set(EXECUTABLE_NAME "main")

list(APPEND CMAKE_MODULE_PATH "${PROJECT_SOURCE_DIR}/cmake")

include(FetchContent)
include(Tools)

add_cmake_format_target()
add_clang_format_target()

message("Using FetchContext")
                     

FetchContent_Declare(
    zeromq
    GIT_REPOSITORY https://github.com/zeromq/libzmq.git
    GIT_TAG v4.3.4
    GIT_SHALLOW TRUE
    OVERRIDE_FIND_PACKAGE
)   
option(ENABLE_DRAFTS ON CACHE)
option(BUILD_TESTS OFF)
FetchContent_MakeAvailable(zeromq)

#-----------------------------------------------------------------------------
# default to Release build

if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release CACHE STRING
      "Choose the type of build, options are: None Debug Release RelWithDebInfo MinSizeRel."
      FORCE)
endif(NOT CMAKE_BUILD_TYPE)

add_subdirectory(configured)
add_subdirectory(src)


