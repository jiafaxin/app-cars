package com.autohome.app.cars.job.common;

import com.autohome.app.cars.common.utils.StringUtils;
import com.autohome.job.core.biz.model.ReturnT;
import com.autohome.job.core.executor.XxlJobExecutor;
import com.autohome.job.core.handler.IJobHandler;
import com.autohome.job.core.log.XxlJobFileAppender;
import com.autohome.job.core.util.ShardingUtil;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobTestTool {
    public static void Testing(ApplicationContext applicationContext) {
        XxlJobExecutor bean = applicationContext.getBean(XxlJobExecutor.class);
        bean.destroy();

        System.out.println("---------------------Start Debug JobHandlers------------------------------------------------------------------------");
        System.out.println("**  在当前测试模式下， 在上面发现 ExecutorRegistryThread 类似这种错误，可以忽略掉。因为到这里已经干掉了与JobServer通信的线程");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        XxlJobFileAppender.contextHolder.set(null);

        ConcurrentHashMap<String, IJobHandler> all = XxlJobExecutor.AllHandler();
        System.out.println("所有处理程序(" + all.keySet().size() + ")：");

        for (int i = 0; i < all.keySet().size(); i++) {
            System.out.println(i + ":" + all.keySet().toArray()[i]);
        }


        System.out.println("选择相应的编辑或@JobHander Value 回车执行 :  按q 退出");

        String line = "";
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (!(line = buffer.readLine()).equals("q")) {

                System.out.println("输入参数，使用逗号(,)分隔，回车确认.");

                String strparam = null;

                strparam = buffer.readLine();

                try {
                    IJobHandler iJobHandler = null;
                    if(isNumeric(line)){
                        int index = Integer.parseInt(line);
                        iJobHandler = (IJobHandler) all.values().toArray()[index];
                    } else {
                        iJobHandler = all.get(line);
                    }
                    ShardingUtil.setShardingVo(new ShardingUtil.ShardingVO(0, 1));
                    ReturnT<String> result = iJobHandler.execute(strparam.split(","));
                    ShardingUtil.setShardingVo(null);

                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println(e);
                }

                System.out.println();
                System.out.println();

                System.out.println("-------------------------------------------------------------");
                System.out.println("选择相应的编辑回车执行 :  按q 退出");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("已经完成退出 ....");

    }

    public static boolean isNumeric(String str) {
        return StringUtils.isInteger(str);
    }
}
