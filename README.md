# LiFR
**LiFR** is a **Li**ghweight **F**uzzy semantic **R**easoner. It implements a fuzzy extension of ![](https://wikimedia.org/api/rest_v1/media/math/render/svg/2302a18e269dbecc43c57c0c2aced3bfae15278d)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/19ef4c7b923a5125ac91aa491838a95ee15b804f)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/0e9730a0ada0426927ff64141eb9f505eca132d4)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/4e63ea009de5efbca2fc285b8550daaed577c6b8) <sup>-</sup>.

------------


## Description ##
The algorithmic foundation of LiFR lies in the crisp reasoner it has extended: the [Pocket KRHyper](http://ceur-ws.org/Vol-189/submission_36.pdf) reasoner, a reasoner initially developed for first generation mobile devices. Thus it is a first-order model generating reasoner implementing the [hyper-tableaux calculus](http://link.springer.com/chapter/10.1007/3-540-69778-0_14). DL reasoning in Pocket KRHyper is performed through translating DLs axioms to ﬁrst order clauses, as portrayed in the translation primitives of Pocket KRHyper. The original [Description Logic (DL)](http://arxiv.org/pdf/1201.4089.pdf) interface provided by Pocket KRHyper was extended to support added semantics and fuzzy operators’ transformation into the native ﬁrst order clausal implementation. 

LiFR has extended Pocket KRHyper to fuzziness and has made improvements on the original implementation efficiency-wise and with respect to disjunction handling. The first extension within CERTH, named initially *f*-PocketKRHyper, was a J2ME application like its crisp predecessor. Since then, it was transformed to JavaSE with an interest in rendering it capable of multi-platform implementations, while maintaining the original implementation's principles of a lightweight and efficient algorithm, capable of performing reasoning services *in every device*, including in limited resource devices, such as smartphones, tablets, set-top boxes etc.

The general inferencing services provided by LiFR are:

 - Consistency checking
 - Satisfiability checking
 - Concept subsumption
 - Fuzzy entailment
 - Best entailment degree (BED) calculation

Extending Pocket KRHyper, LiFR’s default reasoning service consists in the generation of all models that satisfy the input fuzzy knowledge base, thereby providing native support for the computation of the BED for all combinations of individuals and concepts.

### Relevant publication ###
Tsatsou, D., Dasiopoulou, S., Kompatsiaris, I., & Mezaris, V. (2014, May). LiFR: a lightweight fuzzy DL reasoner. In European Semantic Web Conference (pp. 263-267). Springer International Publishing. 

### LiFR Semantics and Syntax ###
LiFR initially supported fuzzy DLP (f-DLP) semantics (as seen in the aforementioned publication). f-DLP is the fuzzy extension of [DLP](http://www.cs.man.ac.uk/~horrocks/Publications/download/2003/p117-grosof.pdf), a tractable knowledge representation fragment, closely related to the [OWL 2 RL](http://www.w3.org/TR/owl2-profiles/#OWL_2_RL) proﬁle, that combines classical DLs with Logic Programs (LP), thus combining ontologies with rules.

Since then, LiFR is extended beyond Horn clauses, since it supports definite clauses with complex heads. In addition, it is extended to support complex negation, therefore placing it within the ![](https://wikimedia.org/api/rest_v1/media/math/render/svg/2302a18e269dbecc43c57c0c2aced3bfae15278d)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/19ef4c7b923a5125ac91aa491838a95ee15b804f)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/0e9730a0ada0426927ff64141eb9f505eca132d4)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/4e63ea009de5efbca2fc285b8550daaed577c6b8) <sup>-</sup> fragment, which is a sub-language of  ![](https://wikimedia.org/api/rest_v1/media/math/render/svg/2302a18e269dbecc43c57c0c2aced3bfae15278d)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/19ef4c7b923a5125ac91aa491838a95ee15b804f)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/0e9730a0ada0426927ff64141eb9f505eca132d4)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/4e63ea009de5efbca2fc285b8550daaed577c6b8), with limited universal restrictions.

The fuzzy extension of ![](https://wikimedia.org/api/rest_v1/media/math/render/svg/2302a18e269dbecc43c57c0c2aced3bfae15278d)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/19ef4c7b923a5125ac91aa491838a95ee15b804f)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/0e9730a0ada0426927ff64141eb9f505eca132d4)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/4e63ea009de5efbca2fc285b8550daaed577c6b8) <sup>-</sup> (*f*-![](https://wikimedia.org/api/rest_v1/media/math/render/svg/2302a18e269dbecc43c57c0c2aced3bfae15278d)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/19ef4c7b923a5125ac91aa491838a95ee15b804f)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/0e9730a0ada0426927ff64141eb9f505eca132d4)![](https://wikimedia.org/api/rest_v1/media/math/render/svg/4e63ea009de5efbca2fc285b8550daaed577c6b8) <sup>-</sup>) lies in the support of fuzzy assertions, restricted to concepts only, with an added support for weighted concept modifiers, while role assertions are currently treated as crisp with an imposed membership degree of ≥ 1.0 . The crisp operations intersection, union and implication, are extended to fuzzy sets and performed by t-norm, t-conorm and implication functions respectively. The fuzzy set operations of LiFR follow the operators of [Zadeh fuzzy logic](http://www-bisc.cs.berkeley.edu/Zadeh-1965.pdf). 

LiFR's syntax follows a lisp-like variant of the [KRSS](http://dl.kr.org/dl97/krss.ps) ontological notation. 


