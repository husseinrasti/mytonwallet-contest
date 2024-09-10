import React, { memo } from '../../lib/teact/teact';
import { getActions, withGlobal } from '../../global';

import type { NotificationType } from '../../global/types';

import { pick } from '../../util/iteratees';

import Notification from '../ui/Notification';

type StateProps = {
  notifications: NotificationType[];
};

function Notifications({ notifications }: StateProps) {
  const { dismissNotification } = getActions();

  if (!notifications.length) {
    return undefined;
  }

  return (
    <div id="Notifications">
      {notifications.map(({ message, icon }) => (
        <Notification
          icon={icon}
          message={message}
          // eslint-disable-next-line react/jsx-no-bind
          onDismiss={dismissNotification}
        />
      ))}
    </div>
  );
}

export default memo(withGlobal(
  (global): StateProps => pick(global, ['notifications']),
)(Notifications));
