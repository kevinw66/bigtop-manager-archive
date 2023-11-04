package org.apache.bigtop.manager.server.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class HostAddEvent extends Event {

    Long jobId;

    Long clusterId;

    List<String> hostnames;

}
