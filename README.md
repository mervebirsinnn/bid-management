# Bid Management

Jakarta EE, WildFly ve WebSocket (Jakarta Faces Push) öğrenmek için yazılmış küçük bir teklif (bid) yönetimi uygulaması. Bir sekmede teklif kaydedildiğinde, açık olan tüm sekmelerdeki tablo sayfa yenilenmeden anında güncellenir.

## Kullanılan teknolojiler

| Teknoloji | Kullanım amacı |
|-----------|----------------|
| Java 21 | Dil ve derleme hedefi |
| Jakarta EE 10 | Platform API'leri |
| WildFly 41 | Uygulama sunucusu |
| Jakarta Faces (JSF) | Sunucu taraflı arayüz (`bids.xhtml`) |
| Faces Push / `<f:websocket>` | WebSocket ile gerçek zamanlı bildirim |
| PrimeFaces 15 | UI bileşenleri (`p:dataTable`, `p:remoteCommand`, `p:inputNumber` ...) |
| CDI | Bağımlılık enjeksiyonu, scope'lar ve event'ler |
| Jakarta Persistence (JPA) + Hibernate | Veritabanı erişimi |
| Jakarta Transactions (JTA) | `@Transactional` ile transaction yönetimi |
| Jakarta REST (JAX-RS) | `/api` altında REST uçları |
| Jakarta Concurrency | `ManagedExecutorService` ile container yönetimli thread |
| Maven (+ wrapper) | Build, WAR paketleme |

## Nasıl çalışır?

```
Save bid  ->  BidView.save()
          ->  BidService.create()        (@Transactional, kaydı yazar ve BidCreated event'i fırlatır)
          ->  commit
          ->  BidNotifier.afterCommit()  (@Observes AFTER_SUCCESS)
          ->  PushContext.send("BID_CREATED")  ->  "bids" kanalı (WebSocket)
          ->  bids.xhtml <f:websocket>   ->  onBidMessage()  ->  refreshBids()  ->  tablo yenilenir
```

- `BidNotifier`, bildirimi transaction başarıyla commit edildikten sonra gönderir; rollback olursa kimseye bildirim gitmez.
- Gönderim işi `ManagedExecutorService` ile sunucunun yönettiği ayrı bir thread'e verilir.
- WebSocket endpoint'i `web.xml` içindeki `jakarta.faces.ENABLE_WEBSOCKET_ENDPOINT=true` parametresiyle açılır.

## Proje yapısı

```
src/main/java/com/merve/
  RestApplication.java        JAX-RS giriş noktası (/api)
  bid/
    Bid.java                  JPA entity
    BidCreated.java           CDI event (record)
    BidService.java           İş mantığı ve veritabanı işlemleri
    BidView.java              Faces backing bean
    BidNotifier.java          Commit sonrası WebSocket bildirimi
    BidResource.java          REST uçları (/api/bids)
    HelloResource.java        Basit test ucu (/api/hello)
src/main/resources/META-INF/persistence.xml
src/main/webapp/bids.xhtml    Arayüz
src/main/webapp/WEB-INF/web.xml
```

## Çalıştırma

Gereksinimler: JDK 21 ve WildFly (varsayılan `ExampleDS` veri kaynağı ile).

```powershell
.\mvnw.cmd clean package
Copy-Item .\target\bid-management.war <WILDFLY_HOME>\standalone\deployments\ -Force
```

WildFly çalışırken WAR dosyası otomatik deploy edilir. Sonra şu adresleri aç:

- Arayüz: http://localhost:8080/bid-management/bids.xhtml
- REST: http://localhost:8080/bid-management/api/bids

WebSocket'i denemek için sayfayı iki sekmede aç, birinde teklif kaydet, diğerinin tablosunun anında güncellendiğini gör.

> Not: Yeniden deploy sonrası eski sekmeler `ViewExpiredException` verir, sayfayı yenilemek gerekir.
