set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR}/bin)
set(LIBRARY_OUTPUT_PATH  ${CMAKE_BINARY_DIR}/lib)

set(ZMQJ_SOURCES 
	"${CMAKE_CURRENT_SOURCE_DIR}/socket.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/context.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/util.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/ZMQ.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/ZMQEvent.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/curve.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/poller.cpp"
	"${CMAKE_CURRENT_SOURCE_DIR}/zframe.cpp"
)
set(ZMQJ_HEADERS "${CMAKE_CURRENT_SOURCE_DIR}/socket.h")
set(ZMQJ_INCLUDES 
	"./" 
	"${CMAKE_BINARY_DIR}/configured_files/include"
	"${CMAKE_BINARY_DIR}/src" 
)

set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
set(JAVA_INCLUDE_PATH2 NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)

#-----------------------------------------------------------------------------
# platform specifics

add_definitions(
    -DWIN32
	-DDLL_EXPORT
	-DFD_SETSIZE=1024
)

set(java-sources-dir
    ../../../lib/src/main/java/org/zeromq
)
set(javah-headers
	org_zeromq_ZMQContext.h
	org_zeromq_ZMQSocket.h
	org_zeromq_ZMQ.h
	org_zeromq_ZMQEvent.h
	org_zeromq_ZMQCurve.h
	org_zeromq_ZMQPoller.h
	org_zeromq_ZFrame.h
)





add_library(${ZMQJ} SHARED ${ZMQJ_SOURCES}  ${ZMQJ_HEADERS} ${javah-headers})
target_include_directories(${ZMQJ} PUBLIC ${ZMQJ_INCLUDES} ${JNI_INCLUDE_DIRS})
target_link_libraries(
    ${ZMQJ}
    libzmq
)

add_custom_command(
    TARGET ${ZMQJ}  PRE_BUILD
	COMMAND ${CMAKE_COMMAND} -E remove *.h
	WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
)

add_custom_command(
	TARGET ${ZMQJ}  PRE_BUILD
	COMMAND ${Java_JAVAC_EXECUTABLE} -h . ${java-sources-dir}/*.java
	WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
)

 
get_filename_component(JAVA_NATIVE_RESOURCES_PATH 
	"../../../zmqj/lib/src/main/resources/NATIVE" 
	ABSOLUTE
)

message(STATUS ${JAVA_NATIVE_RESOURCES_PATH})

if(${CMAKE_SYSTEM} MATCHES Linux)
	message(STATUS "linux platform")
	add_custom_command(
        TARGET ${ZMQJ}  POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/_deps/zeromq-build/lib/libzmq.so
                ${JAVA_NATIVE_RESOURCES_PATH}/Linux/x86_64/libzmq.so
		WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR})
	add_custom_command(
        TARGET ${ZMQJ}  POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy
                ${CMAKE_BINARY_DIR}/lib/libzmqj.so
                ${JAVA_NATIVE_RESOURCES_PATH}/Linux/x86_64/libzmqj.so)

elseif(${CMAKE_SYSTEM} MATCHES Windows)
	message(STATUS "windows platform")
elseif(${CMAKE_SYSTEM} MATCHES Darwin)
	message(STATUS "mac platform")
else()
	message(FATAL_ERROR "No implementation for the platform")
endif()

add_custom_command(
        TARGET ${ZMQJ}  POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E remove
                ${java-sources-dir}/*.class
                )

# if(WIN32)      
	
# elseif(APPLE)
# 	message(STATUS "mac platform")

# elseif(UNIX)
# 	message(STATUS "linux platform")
# 	add_custom_command(
#         TARGET ${ZMQJ}  POST_BUILD
#         COMMAND ${CMAKE_COMMAND} -E copy
#                 ${CMAKE_BINARY_DIR}/lib/libzmqj.so
#                 ${JAVA_NATIVE_RESOURCES_PATH}/Linux/x86_64/libzmqj.so)

# else()
#     message(FATAL_ERROR "No implementation for the platform")
# endif()


