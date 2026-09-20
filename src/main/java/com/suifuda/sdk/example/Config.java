package com.suifuda.sdk.example;

/**
 * 示例配置（代码常量，正式接入请替换为自己的密钥）。
 *
 * @author 张丹峰
 * @since 2026/9/17
 */
public final class Config {

    /**
     * 测试环境
     */
    public static final String TEST_BASE_URL = "https://open-api.test.suifuda.com";

    /**
     * 生产环境
     */
    public static final String PROD_BASE_URL = "https://open-api.suifuda.com";


    /**
     * 当前使用的环境地址（切换测试/生产时改这里）
     */
    public static final String BASE_URL = TEST_BASE_URL;

    public static final String APP_KEY = "SFD_000001";
    public static final String SIGN_TYPE = "RSA";
    public static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDvK1u9rLkahnHhVTiC+aI2EDmrlWvnKKkXkkdEMef4mNYz1egIKbtL1VKYWkoVREw4vU/KV89uZsUF9x3wlegOSqAPaK7B+6AUHVTLos2CjM3KtW8gaTyNWniQEmz1chkZlU9CpmmxFVYIzP/2IAHFnSKKF/S0oWEgxFFGtVZdlfh1layXYhCuL4dGB44qUXCjlBXkWrwnYASxJcrCMm5PsK1JRyDDQkoAZOe/cDciRdNum66/flAwr3Eu++rWTYHlIf/1GMJUVxLkub1xetsRN7FatY1ctgufiDeeJJu0DAEYx9roCBHEdfyY7+S9f/n+t7gqxRTtL1GnAnGoef2zAgMBAAECggEABPdYrtNqKTesdVowfmESR17Gi8kNALOdReeG16QqUG+qrFCJ7Q13cvzUJmS4OsEnIgXQT9UgnLFr8D1FimGFJt18KFsV8eI4hze6szByEFQTJofmQv0xwYT6PDzzCpUFb2dkdMUBgdNTF4NbjJqf3stt/wG+AOLZokq2nf9q8oN96jOc/tJfwjE6mkeAEcrU8CxgM+jzWcrN9sf/MLx/hVnldZI1E7bBYKrMquPTxehOa4CorF9DWR5alnzdzBuywEFJEzEmF9XuXM8grq3Z96NRnq5PFo5uFOks+P171Z5BE6ZJJ+LuALyYs/e/plnJmlYAY6dgOtoieXJpn7D35QKBgQDydgKutN9JOy7i5JFTFkziFfJUyaFkNafkUTeTJCd/FvwvkV1hicrx9lZ4FyB4PGvkoD0Cq6+xMko50IdDe68Ag37iA7yrAwmGs3g9vgOWiW6dxhGtArnpcurC4JfcLLiKFiyNXhfsZGEGrmIe6YVhYyIi2LvtwZ0kNCzZjlIs1QKBgQD8hktP8t7ZRtv/Tciv6IyeSqouKQYMJhzhFmoReqwKzzd1UmQXV5jP4uTkidZIlsNkEnxcr1Tlt7/p/MRs+pWws86y4BrKiUOaSZ6GmpCi5+x1DuZznLOOT36IPgtCsRp1jf+DbyVvpsPUWycZjdpvsyRzDaq9Fx9s9IBs2KwkZwKBgQC2vfFJojOEq2GsSx+pIu6xnF74Prwwniw7bmdXtHE8XaOXapWaNVPLyKHlg3DKjqe3Onqhb6tM/51oM94vI6KD3ZgdWzmxHunoJ45h4rwrksvXiHi8EIj1BdMhLEvo+5/fG9Vae2fGmOV0NqznpZQNEb2hy/7gtQPuTJDdLGKNPQKBgFAoRNG881Ye8whoZXLJK5G3e6upY/0JQBOG7OZdfSz1N9Jq89ChAqXCjQuqYTWrfZxPbeBx204L9MROcv9wz4FGoi8PihGOXsIj6kkp2Q2M9vb1YtuB+1EKFM0kfKiP1SlJiLjs2AJH+lN3F8M5zwOz5rcKuI7LPzNzrSxf3syzAoGBAOvkhvk4/CocLShQwFTYGl388sN1v+8ALo8hNjN4ioAxllHScQ2wJq/zmyhYYxWY4dmL+Etx/84vIiK33MAu+k1fejrN6JxQfuln5tWbWvGSAGf9NswM5QYQIBkb9vZ1GvLXWFTW1LxLhpf+ixKwX4CRppHqv1tb8PrlDZkxqFRg";
    public static final String PLATFORM_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAn1Tm4nk2radH2dLqJ8Ye7Pd8zJHHMTT98xkTeNBWr4TuK7Bcnnp0E4UjlEf8Maf+/9+Euug4DKgTgbXM6gCezFYeIglioeGxogGtchn+OaoRdgrtKBeTYZ1z/0jsidLXy3A42rQhpqJ2V3nijwJGW2zPNHgAYRvYHQXupCDYdkKc2mk6TmCwwuwwN7Sb8Spr2rY3oDQxQCeHS1cgDc6nYeuSrMFU6Emn58F5p+VhqQ7asuh3qzzV0Z0wFmSX2stOj01yPHhs/wBcX8Nt99vmvidVeszc8dY5Fz7uVyF+jGjA0s7f8WupgJqFtvlhwMOtGoXv1RfEX4K3lCqxKjgldwIDAQAB";

//    public static final String APP_KEY = "SFD_000002";
//    public static final String SIGN_TYPE = "SM";
//    public static final String PRIVATE_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgK+gaiHORQN/S7tCDWRBKjMDP70IAihIlGhT3RSMhWQigCgYIKoEcz1UBgi2hRANCAAQYMlyalucFXrsLzA6TPvow5W4nZcWFSibUENHB/+sFc6UcbQvZ8lMi1BrUARDKlXTEyfpp2Qb+PMEG/Ca/1amg";
//    public static final String PLATFORM_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEbJL517CHMjI6O3bZgW88DPzoHea6vwptswHiqqXhMhOgi2QB//jix/LPFlJ8KayeoZIWQrfB4aH8TWmJv5fAMw==";


    private Config() {
    }
}
