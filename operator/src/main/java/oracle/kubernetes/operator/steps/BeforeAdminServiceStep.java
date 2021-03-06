// Copyright 2017, 2018, Oracle Corporation and/or its affiliates.  All rights reserved.
// Licensed under the Universal Permissive License v 1.0 as shown at
// http://oss.oracle.com/licenses/upl.

package oracle.kubernetes.operator.steps;

import java.util.List;
import oracle.kubernetes.operator.ProcessingConstants;
import oracle.kubernetes.operator.helpers.DomainPresenceInfo;
import oracle.kubernetes.operator.work.NextAction;
import oracle.kubernetes.operator.work.Packet;
import oracle.kubernetes.operator.work.Step;
import oracle.kubernetes.weblogic.domain.v1.Domain;
import oracle.kubernetes.weblogic.domain.v1.DomainSpec;
import oracle.kubernetes.weblogic.domain.v1.ServerStartup;

public class BeforeAdminServiceStep extends Step {
  public BeforeAdminServiceStep(Step next) {
    super(next);
  }

  @Override
  public NextAction apply(Packet packet) {
    DomainPresenceInfo info = packet.getSPI(DomainPresenceInfo.class);

    Domain dom = info.getDomain();
    DomainSpec spec = dom.getSpec();

    packet.put(ProcessingConstants.SERVER_NAME, spec.getAsName());
    packet.put(ProcessingConstants.PORT, spec.getAsPort());
    List<ServerStartup> ssl = spec.getServerStartup();
    if (ssl != null) {
      for (ServerStartup ss : ssl) {
        if (ss.getServerName().equals(spec.getAsName())) {
          packet.put(ProcessingConstants.NODE_PORT, ss.getNodePort());
          break;
        }
      }
    }
    return doNext(packet);
  }
}
