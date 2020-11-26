package fun.enhui.userconsumer.fallback;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import fun.enhui.userconsumer.api.ConsumerApi;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.stereotype.Component;
import sun.awt.CausedFocusEvent;

import java.util.Map;

/**
 *
 * @author 胡恩会
 * @date 2020/11/26 23:22
 */
@Component
public class UserProviderBackFactory implements FallbackFactory<ConsumerApi> {

    @Override
    public ConsumerApi create(Throwable throwable) {
        return new ConsumerApi() {
            @Override
            public Map<Integer, String> getMap(Integer id) {
                return null;
            }

            @Override
            public Map<Integer, String> getMap2(Integer id, String name) {
                return null;
            }

            @Override
            public Map<String, String> getMap3(Map<String, Object> map) {
                return null;
            }

            @Override
            public Map<String, String> postMap(Map<String, Object> map) {
                return null;
            }

            @Override
            public String alive() {
                if (throwable instanceof FeignException.InternalServerError) {
                    return "远程服务器500了";
                }
                System.out.println(throwable);
                return ToStringBuilder.reflectionToString(throwable);
            }
        };
    }
}
