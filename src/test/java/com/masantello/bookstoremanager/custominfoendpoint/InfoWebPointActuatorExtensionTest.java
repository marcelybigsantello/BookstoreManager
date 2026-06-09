package com.masantello.bookstoremanager.custominfoendpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.WebEndpointResponse;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = InfoWebPointActuatorExtension.class)
public class InfoWebPointActuatorExtensionTest {

    @Autowired
    private InfoWebPointActuatorExtension infoWebPointActuatorExtension;

    @MockitoBean
    private InfoEndpoint infoEndpoint;

    private Map<String, Object> delegateInfo;

    @BeforeEach
    void setUp() {
        delegateInfo = new HashMap<>();
        delegateInfo.put("app", new HashMap<>());
    }

    @Test
    @DisplayName("Should return custom info with status 200")
    void testInfoReturnsCustomInfoWithStatus200() {
        // Arrange
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        WebEndpointResponse<Map<String, Object>> response = infoWebPointActuatorExtension.info();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should add custom info to the response")
    void testInfoAddsCustomInfoToResponse() {
        // Arrange
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        WebEndpointResponse<Map<String, Object>> response = infoWebPointActuatorExtension.info();

        // Assert
        assertThat(response.getBody())
                .containsKey("customInfo")
                .containsEntry("customInfo", "Customizing Spring Boot Actuator Info Endpoint");
    }

    @Test
    @DisplayName("Should preserve original info entries")
    void testInfoPreservesOriginalEntries() {
        // Arrange
        delegateInfo.put("originalKey", "originalValue");
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        WebEndpointResponse<Map<String, Object>> response = infoWebPointActuatorExtension.info();

        // Assert
        assertThat(response.getBody())
                .containsEntry("originalKey", "originalValue");
    }

    @Test
    @DisplayName("Should include both original and custom info")
    void testInfoIncludesBothOriginalAndCustomInfo() {
        // Arrange
        delegateInfo.put("version", "1.0.0");
        delegateInfo.put("name", "Bookstore Manager");
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        WebEndpointResponse<Map<String, Object>> response = infoWebPointActuatorExtension.info();

        // Assert
        Map<String, Object> body = response.getBody();
        assertThat(body)
                .hasSize(4) // version, name, app, customInfo
                .containsKeys("version", "name", "app", "customInfo")
                .containsEntry("customInfo", "Customizing Spring Boot Actuator Info Endpoint");
    }

    @Test
    @DisplayName("Should handle empty delegate info")
    void testInfoHandlesEmptyDelegateInfo() {
        // Arrange
        when(infoEndpoint.info()).thenReturn(new HashMap<>());

        // Act
        WebEndpointResponse<Map<String, Object>> response = infoWebPointActuatorExtension.info();

        // Assert
        assertThat(response.getBody())
                .containsKey("customInfo")
                .hasSize(1);
    }

    @Test
    @DisplayName("Should not modify original delegate info")
    void testInfoDoesNotModifyOriginalDelegateInfo() {
        // Arrange
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        infoWebPointActuatorExtension.info();

        // Assert - delegateInfo should not be modified
        assertThat(delegateInfo).doesNotContainKey("customInfo");
    }

    @Test
    @DisplayName("Should return consistent status code")
    void testInfoReturnsConsistentStatusCode() {
        // Arrange
        when(infoEndpoint.info()).thenReturn(delegateInfo);

        // Act
        WebEndpointResponse<Map<String, Object>> response1 = infoWebPointActuatorExtension.info();
        WebEndpointResponse<Map<String, Object>> response2 = infoWebPointActuatorExtension.info();

        // Assert
        assertThat(response1.getStatus()).isEqualTo(response2.getStatus()).isEqualTo(200);
    }

}
