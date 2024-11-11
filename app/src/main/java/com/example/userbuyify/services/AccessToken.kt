package com.example.userbuyify.services

import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object AccessToken {

    private  val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken():String?{

        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"buyify-fe90b\",\n" +
                    "  \"private_key_id\": \"f5d3eb634013331bb7e441470b238f5d6b63af84\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCr6znPhPjLit8n\\nxbJrjbc/VRL4dQ4Ertr1l6t66f2idUIOi9RKj+84cDo78YBstvKAUoThgEHbhHu2\\nKOAApn9AqTXbpGdWAof4umEBvErA3huOVOYpbkBoRssxDFFRbbDat8bqJCAmE3jg\\nkh3qPSLMSdgdbx5u3eepLPn6krPzinwD5d1u4yTCmsqlGds1Vp8Ex59hFfyZ9Re+\\n0JDO84ka8XNBGltpUYVxTh7zpbHh4LmUcrZ9rJuZtT9qaBZ4T96SwGqgYVku/d0N\\nNKCl4/9GVcUKZogoV/TM+KLUNHS/Kg8U11YOCHCxc+zWLTtIJmJaMBFDJkRi0xaU\\n1rLMdmVvAgMBAAECggEAAnMAc4IRH/040/06CdQ5XG/0LDv8A3m7lCzL/Ef3HWhk\\nP4bNyHN9KfsB/ragaMFrz64ynG/SVbPxJerhKME2e4+tkmp+L7iA2BH3rdRROvwv\\nElEH/REdXnRGaCAlIcQun9RnGLuDsGct8q0XkK28StgbzxeKgcksMrnO+eXl0Gi5\\nAlYQx3+pAtEPxE/ISMdPpq/28wb4F+hGoTQIB840kLFNrUwjJu+TEeaTT7hq7s46\\nJST2O30bMWUXQHY6wfgEmZNDSbf75UUhA4uC1Sdcs+9xoiMUttjaIRlBU2YhkZh/\\n0+hldxqLx85Cdx3NHcfaY+wXLSRRzSChLXDuCSI5IQKBgQDdqgcUeiYMRtoXuWQ8\\nuIxpTpXA/+CxjZsw3Hel6Mhxz8wtX32LREeO/WCN25bxzZJB3ZtdsaDl0L+u5iI1\\nJaG5eGZD4olqZTuTwQHPzpBbD8PpvSFB4bSNk5d+iLr94qn/m1mUGBX3ea9BVTqf\\nqV790dgHp5QfNSBFuWPdfxWdUQKBgQDGjJMT1l24qT985RHbYtpcR/w6PB+wzkiS\\nfFnb3Q4zGxt70qyvIXVCi/iRSXxWdK9zxhMzBLWz0daWNulTyaXa0XSxk7oPh5Tg\\nSB/ieyVvNp70AJOM2nP5p2f4s9CSz95ynnH06fig+FQWVXYneq+VN4JrED0poDDb\\nziqutzwmvwKBgQDGsRcBkvAyBvyNUX/5Mc+iwW9Y/cPyQ6V9adHSJNQvuH0jmrnV\\nMRIXqSV4YwJtosBcTAFtPcCk37ZCV0UIcMADmVnJtfFJLo71xYcTN+yLw5jwFrkN\\n7fxC/65HHNbIpvmNBjqIlgJyv1+J55TR8ycTy5qiWeNbCerwtNpAfB7q0QKBgAr7\\ncZMyU0LIkcttvDwfTqa2EJyEANi9wEPh/vJrWsK26CFEOOsC9cJ1jY0zvF3n3GCR\\n/zPeOdK1c6IhbWyGBdeBqGu+GReMz05Mjv7vAtYQ9l/WvFKZLrsLaHPYNlw4hTxf\\nyTvbyTXWJoiJt5xda+LcDCmx30AzLSCrWa8KUZhvAoGBALUCnZ/BF8afWsMqyys0\\n9uOsFqmyOiIeMkpmoV1/CYMmKmNQdRAUK91akbV+875qtCuMdPSVzgVVuJf2vBFL\\nSz7XrpjB9TCBf3ROzMG8Y8u1R5G4F3MN+6xww14MY77I722F4PN3KezsvZsNv3Iw\\no2cvToyU5JI5W/MzCEqHFvVH\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-nvgo5@buyify-fe90b.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"100742321006384548951\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-nvgo5%40buyify-fe90b.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n"
            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))

            val googleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))
            googleCredentials.refresh()

            return googleCredentials.accessToken.tokenValue
        }catch (e: IOException){
            return null
        }

    }
}