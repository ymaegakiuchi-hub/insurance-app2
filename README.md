# insurance-app

保険契約管理アプリ（個人開発ポートフォリオ）

自動車・地震保険の契約管理・保険料計算ロジックを、実務経験を元に個人で再実装したアプリです。

## 構成

- Java 17 / Spring Boot 3.3
- Spring Data JPA + PostgreSQL
- Thymeleaf（画面表示）

## 主な機能

- 顧客登録・一覧（氏名／メール／電話／生年月日、サーバー側バリデーション）
- 契約登録・一覧・満期間近一覧（30日以内）
- 保険料自動計算（年齢8区分 × 等級4区分、BigDecimalで1円単位切り捨て）
- サーバー側バリデーション（年齢上限70歳・等級1〜20・満期日整合性）

## 起動方法

1. PostgreSQLで `insurance_app` という名前のデータベースを作成
2. `src/main/resources/application.properties` の接続情報を自分の環境に合わせて変更
3. 以下を実行

```bash
./mvnw spring-boot:run
```

4. ブラウザで `http://localhost:8080` にアクセス
