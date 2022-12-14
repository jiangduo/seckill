if redis.call("get",KEYS[1]) == ARGV[1] then--key就是第一个参数，argv、就是随机值
    return redis.call("del",KEYS[1])--相同的话就调用删除方法，
else
    return 0--否则就返回0
end