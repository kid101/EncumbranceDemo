# Corda Bakery [Encumbrance]
This CordApp demonstrate the use encumbrance with a fun example of a Bakery.

In our Bakery, cake of various flavours `[vanilla, chocolate, orange, pineapple, strawberry]` and produced and sold with an Expiry associated with it. 

The Bakery can only sell a cake if the Cake has not expired! and a Buyer can only Consume a cake if it's not Expired. 

_**Encumbrance:**_
`The encumbrance state, if present, forces additional controls over the encumbered state, since the platform checks
that the encumbrance state is present as an input in the same transaction that consumes the encumbered state, and
the contract code and rules of the encumbrance state will also be verified during the execution of the transaction.`

**CordApp Info:** In this cordApp, encumbered state is the `Cake` and encumbrance is the `Expiry`. 
This `Expiry` provides more control over the encumbered state i.e. `Cake` with an additional time-lock. 
In this cordApp Expiry is `Covenant` i.e. it travels alongside each iteration of the encumbered state- Expiry will follow Cake where ever it goes. 

We've 2 Types of Node in this CordApp:
**Bakery**,**Buyer** 

***Flows:*** The below mentioned flows can be used via shell to create, sell and consume cake.

Go to Bakery Node, Create Cake of your choice. provide a flavour, an Id to the Cake and the Expiry of the Cake.
The encumbered state i.e. `Cake` refers to its encumbrance i.e. `Expiry `by index, and the referred encumbrance state i.e. `Expiry` is an output state in a particular position on the same transaction that created the encumbered state i.e. `Cake`.
In our case the position is `1` 


**Create Cake**
`flow start CreateCake flavour: "vanilla", cakeId: "1", expireAfterMinutes: 2`


Once the cake has been created, it can be sold to buyers by providing the cakeId, a buyer, to which the cake has to be sold
and a boolean includeEncumbrance to demonstrate what would happen if the `Cake` is Consumed without it's `Expiry`. It'll throw a `missing encumbrance exception.`
This can be done by passing `includeEncumbrance` as `false`
also, if by any chance the cake has expired, the validation will fail and cake will not be sold.
 
**Sell Cake**
`flow start SellCakeInitiator cakeId: "1", buyer: "BuyerA", includeEncumbrance: true`

Go to `BuyerA` or `BuyerB` Node to consume the cake.
The scrumptious cake created by our Corda Bakery can now be consumed by the buyer.
The buyer has to provide the cakeId which it wants to consume. In case, the Cake has expired the Buyer will not be able to consume the cake. 

**Consume Cake**
`flow start ConsumeCake cakeId: "1"`

***Vault Queries***: The below queries can be used to get the `Cake` and `Expiry` from the vault.

**Get All Cakes**
`run vaultQuery contractStateType: net.corda.demo.sc.state.Cake`

**Get All Expiry**
`run vaultQuery contractStateType: net.corda.demo.sc.state.Expiry`

