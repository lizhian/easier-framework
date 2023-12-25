package easier.framework.core.plugin.mq.enums;

public enum MessageServerType {
    redis {
        @Override
        void haha() {

        }
    }, kafka {
        @Override
        void haha() {

        }
    }, mqtt {
        @Override
        void haha() {

        }
    };

    abstract void haha();
}
